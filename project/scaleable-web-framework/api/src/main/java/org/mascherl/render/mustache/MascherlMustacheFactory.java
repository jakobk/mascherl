package org.mascherl.render.mustache;

import com.github.mustachejava.Code;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheResolver;
import com.github.mustachejava.MustacheVisitor;
import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.DefaultCode;
import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.codes.ExtendNameCode;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.render.TemplateMeta;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;

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
        mostOuterTemplate.set(mainTemplate);
        try {
            // TODO find something better here
            // compile and init, but do not cache!
            Mustache mustache = mc.compile(MascherlRenderer.FULL_PAGE_RESOURCE);
            mustache.init();
            if (!templateIndex.containsKey(mainTemplate)) {
                compile(mainTemplate);
            }
            return mustache;
        } finally {
            mostOuterTemplate.remove();
        }
    }

    public String getPageTemplate() {
        return mostOuterTemplate.get();
    }

    public TemplateMeta getTemplateMeta(String templateName) {
        return templateIndex.get(templateName);
    }

    @Override
    public MustacheVisitor createMustacheVisitor() {
        return new MascherlMustacheVisitor();
    }

    private MustacheVisitor createDefaultMustacheVisitor() {
        return super.createMustacheVisitor();
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
    private class MascherlMustacheVisitor extends MustacheVisitorWrapper {

        public MascherlMustacheVisitor() {
            super(createDefaultMustacheVisitor());
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

        private Mustache createContainerWrapper(TemplateContext templateContext, String variable, Mustache mustache) {
            MustacheVisitor visitor = createDefaultMustacheVisitor();
            visitor.write(templateContext, "<div id=\"" + variable + "\" m-page=\"");
            visitor.value(templateContext, "pageId", true);
            visitor.write(templateContext, "\">");
            visitor.name(templateContext, variable, mustache);
            visitor.write(templateContext, "</div>");
            return visitor.mustache(templateContext);
        }

    }

}
