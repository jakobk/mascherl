package org.mascherl.page;

import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.Messages.OUTDATED_VERSION_MSG;
import static org.mascherl.MascherlConstants.RequestParameters.M_APP_VERSION;
import static org.mascherl.MascherlConstants.RequestParameters.M_CLIENT_URL;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_FORM;
import static org.mascherl.MascherlConstants.RequestParameters.M_PAGE;
import static org.mascherl.page.MascherlPageUtils.createUriBuilder;
import static org.mascherl.page.MascherlPageUtils.forwardAsGetRequest;
import static org.mascherl.page.MascherlPageUtils.invokeWithInjectedJaxRsParameters;

/**
 * Interface for all Mascherl page controllers.
 *
 * @author Jakob Korherr
 */
public interface MascherlPage {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public default Response post(@Context HttpServletRequest request,
                                 @Context HttpServletResponse response,
                                 @FormParam(M_FORM) String form,
                                 @FormParam(M_CONTAINER) String container,
                                 @FormParam(M_APP_VERSION) ApplicationVersion clientAppVersion,
                                 @FormParam(M_PAGE) String page) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        if (!mascherlContext.getApplicationVersion().equals(clientAppVersion)) {
            return Response.status(Response.Status.CONFLICT).entity(OUTDATED_VERSION_MSG).build();
        }

        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(getClass());

        Method formMethod = pageClassMeta.getFormMethod(form);
        if (formMethod == null) {
            // no form method found, thus POST is not supported by this page
            throw new WebApplicationException(Response.status(Response.Status.METHOD_NOT_ALLOWED).allow("GET").build());
        }

        Object formMethodResult = invokeWithInjectedJaxRsParameters(this, formMethod);

        UriBuilder uriBuilder = createUriBuilder(formMethodResult);
        if (uriBuilder != null) {
            String clientUrl = uriBuilder.build().toString();
            request.setAttribute(M_CLIENT_URL, clientUrl);

            uriBuilder.queryParam(M_CONTAINER, MAIN_CONTAINER);
            uriBuilder.queryParam(M_APP_VERSION, clientAppVersion.getVersion());
            return forwardAsGetRequest(request, response, uriBuilder);
        } else {
            // adjust container, if available
            if (formMethodResult instanceof ContainerRef) {
                container = ((ContainerRef) formMethodResult).getContainer();
            }

            // no forward necessary
            return get(request, container, clientAppVersion, page);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public default Response get(@Context HttpServletRequest request,
                                @QueryParam(M_CONTAINER) String container,
                                @QueryParam(M_APP_VERSION) ApplicationVersion clientAppVersion,
                                @QueryParam(M_PAGE) String page) {
        final boolean partialRequest = (container != null);

        MascherlContext mascherlContext = MascherlContext.getInstance();
        if (partialRequest && !mascherlContext.getApplicationVersion().equals(clientAppVersion)) {
            return Response.status(Response.Status.CONFLICT).entity(OUTDATED_VERSION_MSG).build();
        }

        MascherlRenderer renderer = mascherlContext.getMascherlRenderer();
        if (partialRequest) {
            if (page != null && !Objects.equals(page, getClass().getName())) {
                container = MAIN_CONTAINER;
            }
            String clientUrl = (String) request.getAttribute(M_CLIENT_URL);
            return renderer.renderContainer(this, container, clientUrl);
        } else {
            return renderer.renderFull(this);
        }
    }

    public default String getTitle() {
        return getClass().getSimpleName();
    }

}
