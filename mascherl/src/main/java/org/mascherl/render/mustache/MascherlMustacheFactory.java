/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.render.mustache;

import com.github.mustachejava.Code;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.DefaultMustacheVisitor;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheResolver;
import com.github.mustachejava.MustacheVisitor;
import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.DefaultCode;
import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.codes.ExtendNameCode;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.TemplateMeta;
import org.mascherl.render.mustache.fullpage.FullPageCachedMustache;
import org.mascherl.render.mustache.fullpage.FullPageDynamicMustache;
import org.mascherl.render.mustache.fullpage.MainContainerPartialCode;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.RootScopeVariables.PAGE_GROUP;
import static org.mascherl.render.MascherlRenderer.FULL_PAGE_RESOURCE;

/**
 * Mascherl specific Mustache factory, which adds Mascherl specific functionality to {@link DefaultMustacheFactory}.
 *
 * @author Jakob Korherr
 */
public class MascherlMustacheFactory extends DefaultMustacheFactory {

    private final ThreadLocal<String> mostOuterTemplate = new ThreadLocal<>();
    private final ConcurrentMap<String, ConcurrentMap<String, Mustache>> templateContainerIndex = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, TemplateMeta> templateIndex = new ConcurrentHashMap<>();

    public MascherlMustacheFactory(ServletContext servletContext) {
        this(new WebApplicationMustacheResolver(servletContext));
    }

    public MascherlMustacheFactory(MustacheResolver mustacheResolver) {
        super(mustacheResolver);
    }

    @Override
    public Mustache compile(String mainTemplate) {
        mostOuterTemplate.set(mainTemplate);
        try {
            Mustache mustache = super.compile(mainTemplate);
            if (!templateIndex.containsKey(mainTemplate)) {
                buildContainerRelationIndex(mainTemplate, mustache);
            }
            return mustache;
        } finally {
            mostOuterTemplate.remove();
        }
    }

    public Mustache compileFullPage(String mainTemplate) {
        Mustache mainContainerMustache = compile(mainTemplate);

        mostOuterTemplate.set(FULL_PAGE_RESOURCE);
        try {
            FullPageCachedMustache fullPage = (FullPageCachedMustache) super.compile(FULL_PAGE_RESOURCE);
            return new FullPageDynamicMustache(fullPage, mainContainerMustache);
        } finally {
            mostOuterTemplate.remove();
        }
    }

    @Override
    protected Function<String, Mustache> getMustacheCacheFunction() {
        return (name) -> {
            Mustache mustache = super.getMustacheCacheFunction().apply(name);
            if (Objects.equals(FULL_PAGE_RESOURCE, name)) {
                mustache = new FullPageCachedMustache(mustache);
            }
            return mustache;
        };
    }

    public TemplateMeta getTemplateMeta(String templateName) {
        if (!templateIndex.containsKey(templateName)) {
            compile(templateName);
        }
        return templateIndex.get(templateName);
    }

    @Override
    public MustacheVisitor createMustacheVisitor() {
        return new MascherlMustacheVisitor();
    }

    public Mustache getMustacheForContainer(String mainTemplate, String containerName) {
        return getContainerIndex(mainTemplate).get(containerName);
    }

    private void captureContainer(String containerName, Mustache containerMustache) {
        String mainTemplate = mostOuterTemplate.get();
        ConcurrentMap<String, Mustache> containerIndex = getContainerIndex(mainTemplate);
        containerIndex.putIfAbsent(containerName, containerMustache);
    }

    private ConcurrentMap<String, Mustache> getContainerIndex(String mainTemplate) {
        ConcurrentMap<String, Mustache> containerIndex = templateContainerIndex.get(mainTemplate);
        if (containerIndex == null) {
            synchronized (templateContainerIndex) {
                containerIndex = templateContainerIndex.get(mainTemplate);
                if (containerIndex == null) {
                    containerIndex = new ConcurrentHashMap<>();
                    templateContainerIndex.put(mainTemplate, containerIndex);
                }
            }
        }
        return containerIndex;
    }

    private void buildContainerRelationIndex(String templateName, Mustache mustache) {
        Map<String, String> containerRelation = new HashMap<>();
        buildContainerRelationRecursive(containerRelation, mustache, MAIN_CONTAINER, true);

        TemplateMeta templateMeta = new TemplateMeta(templateName);
        for (Map.Entry<String, String> relation : containerRelation.entrySet()) {
            String childContainer = relation.getKey();
            String parentContainer = relation.getValue();

            ContainerMeta childMeta = findOrCreateContainerMeta(templateMeta, childContainer);
            ContainerMeta parentMeta = findOrCreateContainerMeta(templateMeta, parentContainer);

            parentMeta.addChild(childMeta);
        }

        if (templateMeta.getContainerMeta(MAIN_CONTAINER) == null) {  // make sure metadata for the main container is there
            templateMeta.addContainer(new ContainerMeta(MAIN_CONTAINER));
        }

        templateIndex.put(templateName, templateMeta);
    }

    private ContainerMeta findOrCreateContainerMeta(TemplateMeta templateMeta, String containerName) {
        ContainerMeta containerMeta = templateMeta.getContainerMeta(containerName);
        if (containerMeta == null) {
            containerMeta = new ContainerMeta(containerName);
            templateMeta.addContainer(containerMeta);
        }
        return containerMeta;
    }

    private void buildContainerRelationRecursive(Map<String, String> containerRelation, Code parentCode, String lastContainer, boolean isParent) {
        if (parentCode.getCodes() != null && parentCode.getCodes().length > 0) {
            for (Code childCode : parentCode.getCodes()) {
                if (childCode instanceof ExtendNameCode) {
                    ExtendNameCode extendNameCode = (ExtendNameCode) childCode;
                    String extendType = getType(extendNameCode);
                    if (Objects.equals("$", extendType)) {
                        String container = extendNameCode.getName();
                        if (isParent) {
                            containerRelation.putIfAbsent(container, lastContainer);
                        } else {
                            containerRelation.put(lastContainer, container);
                        }
                        buildContainerRelationRecursive(containerRelation, childCode, container, isParent);
                    } else if (Objects.equals("<", extendType)) {
                        buildContainerRelationRecursive(containerRelation, childCode, lastContainer, false);
                    } else {
                        buildContainerRelationRecursive(containerRelation, childCode, lastContainer, isParent);
                    }
                } else {
                    buildContainerRelationRecursive(containerRelation, childCode, lastContainer, isParent);
                }
            }
        }
    }

    private String getType(DefaultCode code) {
        try {
            Field typeField = DefaultCode.class.getDeclaredField("type");
            typeField.setAccessible(true);
            return (String) typeField.get(code);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException("Could not get type of DefaultCode " + code);
        }
    }

    /**
     * {@link MustacheVisitor} implementing Mascherl specific functions, i.e. capturing container mustaches
     * of page templates, and handling the inclusion of the main container mustache into the full page template.
     */
    private class MascherlMustacheVisitor extends DefaultMustacheVisitor {

        public MascherlMustacheVisitor() {
            super(MascherlMustacheFactory.this);
        }

        @Override
        public void name(TemplateContext templateContext, String variable, Mustache mustache) {
            Mustache containerWrapper;

            if (Objects.equals(variable, MAIN_CONTAINER)) {
                DefaultMustache mainPartialMustache = new DefaultMustache(
                        templateContext,
                        MascherlMustacheFactory.this,
                        new Code[] {new MainContainerPartialCode(templateContext, MascherlMustacheFactory.this)},
                        "main-container-dynamic-partial");
                containerWrapper = createContainerWrapper(templateContext, variable, mainPartialMustache);
            } else {
                captureContainer(variable, mustache);
                containerWrapper = createContainerWrapper(templateContext, variable, mustache);
            }

            super.name(templateContext, variable, containerWrapper);
        }

        private void mascherlContainer(TemplateContext templateContext, String variable, Mustache containerWrapper) {
            list.add(new MascherlExtendNameCode(templateContext, df, containerWrapper, variable));
        }

        private Mustache createContainerWrapper(TemplateContext templateContext, String variable, Mustache mustache) {
            MascherlMustacheVisitor visitor = new MascherlMustacheVisitor();
            visitor.write(templateContext, "<div id=\"" + variable + "\" m-page=\"");
            visitor.value(templateContext, PAGE_GROUP, true);
            visitor.write(templateContext, "\">");
            visitor.mascherlContainer(templateContext, variable, mustache);
            visitor.write(templateContext, "</div>");
            return visitor.mustache(templateContext);
        }

    }

}
