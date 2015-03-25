package org.mascherl.render;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.Container;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Partial;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mascherl.MascherlConstants.X_MASCHERL_CONTAINER;
import static org.mascherl.MascherlConstants.X_MASCHERL_PAGE;
import static org.mascherl.MascherlConstants.X_MASCHERL_TITLE;
import static org.mascherl.MascherlConstants.X_MASCHERL_URL;
import static org.mascherl.page.MascherlPageUtils.invokeWithInjectedJaxRsParameters;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MascherlPageRenderer {

    public static final String FULL_PAGE_RESOURCE = "/index.html";

    private final MustacheFactory mustacheFactory;
    private final ServletContext servletContext;

    public MascherlPageRenderer(ServletContext servletContext) {
        this.servletContext = servletContext;
        mustacheFactory = new DefaultMustacheFactory();
    }

    public Response renderFull(MascherlPage page) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(page.getClass());

        BufferedReader templateReader = openPageTemplate(FULL_PAGE_RESOURCE);
        Mustache mustache = mustacheFactory.compile(templateReader, FULL_PAGE_RESOURCE);

        return Response.ok().entity(streamOutput(mustache, new PageRendererScope(page, null, pageClassMeta, servletContext, mustacheFactory))).build();
    }

    public Response renderContainer(MascherlPage page, String container, String clientUrl) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(page.getClass());

        Method containerMethod = pageClassMeta.getContainerMethod(container);
        if (containerMethod == null) {
            throw new IllegalStateException("No method annotated with @" +
                    Container.class.getSimpleName() + "(\"" + container + "\") " +
                    "in class " + page.getClass() + " found.");
        }

        final Partial partial = (Partial) invokeWithInjectedJaxRsParameters(page, containerMethod);
        String resourcePath = partial.getTemplate();

        BufferedReader templateReader = openPageTemplate(resourcePath);
        Mustache mustache = mustacheFactory.compile(templateReader, resourcePath);

        Response.ResponseBuilder response = Response.ok();
        response.header(X_MASCHERL_TITLE, page.getTitle());
        response.header(X_MASCHERL_PAGE, getClass().getName());
        response.header(X_MASCHERL_CONTAINER, container);
        if (clientUrl != null) {
            response.header(X_MASCHERL_URL, clientUrl);
        }

        return response.entity(streamOutput(mustache, new PageRendererScope(page, partial, pageClassMeta, servletContext, mustacheFactory))).build();
    }

    private StreamingOutput streamOutput(Mustache mustache, PageRendererScope scope) {
        return (OutputStream outputStream) -> mustache.execute(new OutputStreamWriter(outputStream), scope).flush();
    }

    private BufferedReader openPageTemplate(String pageTemplate) {
        String realPath = servletContext.getRealPath(pageTemplate);
        if (realPath == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Path path = Paths.get(realPath);
        if (Files.isReadable(path)) {
            try {
                return Files.newBufferedReader(path);
            } catch (IOException e) {
                throw new WebApplicationException(e);
            }
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
