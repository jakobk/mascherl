package org.mascherl.render;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.Container;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Partial;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import static org.mascherl.page.MascherlPageUtils.invokeWithInjectedJaxRsParameters;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class PageRendererScope extends HashMap<String, Object> {

    private final MascherlPage pageInstance;
    private final Partial partial;
    private final PageClassMeta pageClassMeta;
    private final ServletContext servletContext;
    private final MustacheFactory mustacheFactory;

    public PageRendererScope(MascherlPage pageInstance, Partial partial, PageClassMeta pageClassMeta, ServletContext servletContext, MustacheFactory mustacheFactory) {
        this.pageInstance = pageInstance;
        this.partial = partial;
        this.pageClassMeta = pageClassMeta;
        this.servletContext = servletContext;
        this.mustacheFactory = mustacheFactory;
    }

    @Override
    public Object get(Object keyObject) {
        final String key = (String) keyObject;

        if (partial != null && partial.getScope().containsKey(key)) {
            return partial.getScope().get(key);
        }
        if (Objects.equals(key, "title")) {
            return pageInstance.getTitle();
        }
        if (key.startsWith("@")) {
            String containerName = key.substring(1);
            return renderSubContainer(containerName);
        }

        return null;
    }

    private String renderSubContainer(String containerName) {
        Method containerMethod = pageClassMeta.getContainerMethod(containerName);
        if (containerMethod == null) {
            throw new IllegalStateException("No method annotated with @" +
                    Container.class.getSimpleName() + "(\"" + containerName + "\") " +
                    "in class " + pageInstance.getClass() + " found.");
        }

        Partial partial = (Partial) invokeWithInjectedJaxRsParameters(pageInstance, containerMethod);
        if (partial != null) {
            try {
                String realPath = servletContext.getRealPath(partial.getTemplate());
                Path path = Paths.get(realPath);
                Mustache mustache = mustacheFactory.compile(Files.newBufferedReader(path), path.toString());
                StringWriter mustacheOutput = new StringWriter();

                mustache.execute(mustacheOutput, new PageRendererScope(pageInstance, partial, pageClassMeta, servletContext, mustacheFactory)).flush();

                mustacheOutput.close();
                return "<div id=\"" + containerName + "\" m-page=\"" + pageInstance.getClass().getName() + "\">" + mustacheOutput.toString() + "</div>";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "<div id=\"" + containerName + "\" m-page=\"" + pageInstance.getClass().getName() + "\"></div>";
        }
    }

    @Override
    public boolean containsKey(Object keyObject) {
        final String key = (String) keyObject;
        return (partial != null && partial.getScope().containsKey(key))
                || (key.startsWith("@") && pageClassMeta.containerExists(key.substring(1)))
                || (Objects.equals(key, "title"));
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }
}
