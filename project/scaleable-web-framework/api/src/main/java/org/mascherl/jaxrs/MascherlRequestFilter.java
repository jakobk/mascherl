package org.mascherl.jaxrs;

import org.mascherl.application.MascherlApplication;
import org.mascherl.session.MascherlSession;
import org.mascherl.session.MascherlSessionHolder;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.mascherl.MascherlConstants.Messages.OUTDATED_VERSION_MSG;
import static org.mascherl.MascherlConstants.RequestParameters.M_APP_VERSION;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MascherlRequestFilter implements ContainerRequestFilter {

    @Context
    private ServletContext servletContext;

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MascherlApplication mascherlApplication = MascherlApplication.getInstance(servletContext);

        verifyApplicationVersion(mascherlApplication, requestContext);

        MascherlSession mascherlSession = mascherlApplication.getMascherlSessionStorage().restoreSession(request);
        request.setAttribute("MASCHERL_SESSION", mascherlSession);
    }

    private void verifyApplicationVersion(MascherlApplication mascherlApplication, ContainerRequestContext requestContext) {
        if (isPartialRequest(requestContext)) {
            ApplicationVersion clientAppVersion = new ApplicationVersion(
                    requestContext.getUriInfo().getQueryParameters().getFirst(M_APP_VERSION));
            if (!mascherlApplication.getApplicationVersion().equals(clientAppVersion)) {
                requestContext.abortWith(Response.status(Response.Status.CONFLICT).entity(OUTDATED_VERSION_MSG).build());
            }
        }
    }

    private static boolean isPartialRequest(ContainerRequestContext requestContext) {
        String container = requestContext.getUriInfo().getQueryParameters().getFirst(M_CONTAINER);
        return (container != null);
    }

}
