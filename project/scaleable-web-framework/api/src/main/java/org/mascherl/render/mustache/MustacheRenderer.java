package org.mascherl.render.mustache;

import com.github.mustachejava.Mustache;
import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.Container;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.render.MascherlRenderer;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
        Response.ResponseBuilder responseBuilder = Response.ok();
        StreamingOutput output = render(page, MAIN_CONTAINER, false);
        return responseBuilder.entity(output).build();
    }

    @Override
    public Response renderContainer(MascherlPage page, String container, String clientUrl) {
        Response.ResponseBuilder responseBuilder = preparePartialResponse(page, container, clientUrl);
        StreamingOutput output = render(page, container, true);
        return responseBuilder.entity(output).build();
    }

    private StreamingOutput render(MascherlPage page, String container, boolean isPartial) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(page.getClass());

        Mustache mustache;
        if (isPartial) {
            mustache = getContainerMustache(container, pageClassMeta.getPageTemplate());
        } else {
            mustache = mustacheFactory.compileFullPage(pageClassMeta.getPageTemplate());
        }

        Method containerMethod = pageClassMeta.getContainerMethod(container);
        if (containerMethod == null) {
            throw new IllegalStateException("No method annotated with @" +
                    Container.class.getSimpleName() + "(\"" + container + "\") " +
                    "in class " + page.getClass() + " found.");
        }
        Model model = (Model) invokeWithInjectedJaxRsParameters(page, containerMethod);

        return (OutputStream outputStream)
                -> doRenderContainer(mascherlContext, mustache, page, model, new OutputStreamWriter(outputStream));
    }

    private Response.ResponseBuilder preparePartialResponse(MascherlPage page, String container, String clientUrl) {
        Response.ResponseBuilder response = Response.ok();
        response.header(X_MASCHERL_TITLE, page.getTitle());
        response.header(X_MASCHERL_PAGE, page.getClass().getName());
        response.header(X_MASCHERL_CONTAINER, container);
        if (clientUrl != null) {
            response.header(X_MASCHERL_URL, clientUrl);
        }
        return response;
    }

    private void doRenderContainer(MascherlContext mascherlContext, Mustache mustache,
                                   MascherlPage page, Model model, Writer writer) throws IOException {
        MustacheRendererScope scope = new MustacheRendererScope(mascherlContext, page, model);
        mustache.execute(writer, scope).flush();
    }

    private Mustache getContainerMustache(String container, String pageTemplate) {
        // first: make sure that the whole page template is fully compiled
        // (if so we get a cache hit in the MustacheFactory, and it won't get compiled again)
        Mustache mustache = mustacheFactory.compile(pageTemplate);
        // then: check if we should only render a sub-container, and directly use the sub-container mustache
        if (isSubContainer(container)) {
            mustache = mustacheFactory.getMustacheForContainer(pageTemplate, container);
        }
        return mustache;
    }

    private boolean isSubContainer(String container) {
        return !Objects.equals(container, MAIN_CONTAINER);
    }

}
