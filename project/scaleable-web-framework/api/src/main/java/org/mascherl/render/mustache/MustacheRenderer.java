package org.mascherl.render.mustache;

import com.github.mustachejava.Mustache;
import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.Container;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.render.MascherlRenderer;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_PAGE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_TITLE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_URL;
import static org.mascherl.page.MascherlPageUtils.invokeWithInjectedJaxRsParameters;

/**
 * MascherlRenderer implementation using Mustache as render engine.
 *
 * @author Jakob Korherr
 */
public class MustacheRenderer implements MascherlRenderer {

    private final MascherlMustacheFactory mustacheFactory;

    public MustacheRenderer(ServletContext servletContext) {
        mustacheFactory = new MascherlMustacheFactory(servletContext);
    }

    @Override
    public Response renderFull(MascherlPage page) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(page.getClass());

        Mustache mustache = mustacheFactory.compile(FULL_PAGE_RESOURCE);

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

        final Model model = (Model) invokeWithInjectedJaxRsParameters(page, containerMethod);
        if (model != null) {
            String pageTemplate = pageClassMeta.getPageTemplate();

            // in any case: make sure the whole page template is fully compiled
            // (if so we get a cache hit in the MustacheFactory, it won't get compiled again)
            Mustache mustache = mustacheFactory.compile(pageTemplate);

            if (!Objects.equals(container, MAIN_CONTAINER)) {
                mustache = mustacheFactory.getMustacheForContainer(pageTemplate, container);
            }

            MustacheRendererScope scope = new MustacheRendererScope(
                    mascherlContext, page, model, pageClassMeta,
                    (subContainer) -> renderSubContainer(mascherlContext, page, subContainer));
            mustache.execute(writer, scope).flush();
        }
    }

}
