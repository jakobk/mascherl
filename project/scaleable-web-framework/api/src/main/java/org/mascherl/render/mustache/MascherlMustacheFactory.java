package org.mascherl.render.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheResolver;
import com.github.mustachejava.MustacheVisitor;
import com.github.mustachejava.TemplateContext;

import javax.servlet.ServletContext;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TODO
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

    @Override
    public MustacheVisitor createMustacheVisitor() {
        return new MustacheVisitorWrapper(super.createMustacheVisitor()) {

            @Override
            public void name(TemplateContext templateContext, String variable, Mustache mustache) {
                captureContainer(variable, mustache);

                MustacheVisitor visitor = createSuperMustacheVisitor();
                visitor.write(templateContext, "<div id=\"" + variable + "\" m-page=\"");
                visitor.value(templateContext, "pageId", true);
                visitor.write(templateContext, "\">");
                visitor.name(templateContext, variable, mustache);
                visitor.write(templateContext, "</div>");

                super.name(templateContext, variable, visitor.mustache(templateContext));
            }

        };
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

    private MustacheVisitor createSuperMustacheVisitor() {
        return super.createMustacheVisitor();
    }

}
