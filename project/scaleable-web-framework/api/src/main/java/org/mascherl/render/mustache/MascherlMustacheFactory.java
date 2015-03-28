package org.mascherl.render.mustache;

import com.github.mustachejava.Code;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheResolver;
import com.github.mustachejava.MustacheVisitor;
import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.DefaultMustache;
import org.mascherl.render.MascherlRenderer;

import javax.servlet.ServletContext;
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

    public MascherlMustacheFactory(ServletContext servletContext) {
        this(new WebApplicationMustacheResolver(servletContext));
    }

    public MascherlMustacheFactory(MustacheResolver mustacheResolver) {
        super(mustacheResolver);
    }

    @Override
    public Mustache compile(String name) {
        mostOuterTemplate.set(name);
        try {
            return super.compile(name);
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
            return mustache;
        } finally {
            mostOuterTemplate.remove();
        }
    }

    public String getPageTemplate() {
        return mostOuterTemplate.get();
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
