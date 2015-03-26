package org.mascherl.render.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.Container;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Partial;
import org.mascherl.render.MascherlRenderer;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
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
 * MascherlRenderer implementation using Mustache as render engine.
 *
 * @author Jakob Korherr
 */
public class MustacheRenderer implements MascherlRenderer {

    private final MustacheFactory mustacheFactory;
    private final ServletContext servletContext;

    public MustacheRenderer(ServletContext servletContext) {
        this.servletContext = servletContext;
        mustacheFactory = new DefaultMustacheFactory();
    }

    @Override
    public Response renderFull(MascherlPage page) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(page.getClass());

        BufferedReader templateReader = openPageTemplate(FULL_PAGE_RESOURCE);
        Mustache mustache = mustacheFactory.compile(templateReader, FULL_PAGE_RESOURCE);

        MustacheRendererScope scope = new MustacheRendererScope(
                mascherlContext, page, null, pageClassMeta,
                (subContainer) -> renderSubContainer(mascherlContext, page, subContainer));

        StreamingOutput streamingOutput = (OutputStream outputStream)
                -> mustache.execute(new OutputStreamWriter(outputStream), scope).flush();
        return Response.ok().entity(streamingOutput).build();
    }

    @Override
    public Response renderContainer(MascherlPage page, String container, String clientUrl) {
        Response.ResponseBuilder response = Response.ok();
        response.header(X_MASCHERL_TITLE, page.getTitle());
        response.header(X_MASCHERL_PAGE, page.getClass().getName());
        response.header(X_MASCHERL_CONTAINER, container);
        if (clientUrl != null) {
            response.header(X_MASCHERL_URL, clientUrl);
        }

        MascherlContext mascherlContext = MascherlContext.getInstance();
        StreamingOutput streamingOutput = (OutputStream outputStream)
                -> doRenderContainer(mascherlContext, page, container, new OutputStreamWriter(outputStream));
        return response.entity(streamingOutput).build();
    }

    private String renderSubContainer(MascherlContext mascherlContext, MascherlPage page, String container) {
        String mustacheOutput;
        try (StringWriter mustacheWriter = new StringWriter()) {
            doRenderContainer(mascherlContext, page, container, mustacheWriter);
            mustacheOutput = mustacheWriter.toString();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

        return "<div id=\"" + container + "\" m-page=\"" + page.getClass().getName() + "\">" + mustacheOutput + "</div>";
    }

    private void doRenderContainer(MascherlContext mascherlContext, MascherlPage page, String container, Writer writer) throws IOException {
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(page.getClass());

        Method containerMethod = pageClassMeta.getContainerMethod(container);
        if (containerMethod == null) {
            throw new IllegalStateException("No method annotated with @" +
                    Container.class.getSimpleName() + "(\"" + container + "\") " +
                    "in class " + page.getClass() + " found.");
        }

        final Partial partial = (Partial) invokeWithInjectedJaxRsParameters(page, containerMethod);
        if (partial != null) {
            String resourcePath = partial.getTemplate();

            BufferedReader templateReader = openPageTemplate(resourcePath);
            Mustache mustache = mustacheFactory.compile(templateReader, resourcePath);

            MustacheRendererScope scope = new MustacheRendererScope(
                    mascherlContext, page, partial, pageClassMeta,
                    (subContainer) -> renderSubContainer(mascherlContext, page, subContainer));
            mustache.execute(writer, scope).flush();
        }
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
