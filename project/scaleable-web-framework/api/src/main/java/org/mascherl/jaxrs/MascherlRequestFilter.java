package org.mascherl.jaxrs;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mascherl.application.MascherlApplication;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.mascherl.MascherlConstants.RequestParameters.M_APP_VERSION;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;

/**
 * Implementation of {@link javax.ws.rs.container.ContainerRequestFilter} for executing Mascherl specific tasks
 * before a resource method is called.
 *
 * @author Jakob Korherr
 */
public class MascherlRequestFilter implements ContainerRequestFilter {

    private static final String OUTDATED_VERSION_MSG_CONFIG = "org.mascherl.message.outdatedVersion";

    @Context
    private ServletContext servletContext;

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MascherlApplication mascherlApplication = MascherlApplication.getInstance(servletContext);

        verifyApplicationVersion(mascherlApplication, requestContext);

        restoreSession(mascherlApplication);
    }

    private void restoreSession(MascherlApplication mascherlApplication) {
        mascherlApplication.getMascherlSessionStorage().restoreSession(request);
    }

    private void verifyApplicationVersion(MascherlApplication mascherlApplication, ContainerRequestContext requestContext) {
        if (isPartialRequest()) {  // TODO does not work for POST requests --> use HTTP headers for container and version??
            ApplicationVersion clientAppVersion = new ApplicationVersion(
                    requestContext.getUriInfo().getQueryParameters().getFirst(M_APP_VERSION));
            if (!mascherlApplication.getApplicationVersion().equals(clientAppVersion)) {
                Config config = ConfigFactory.load();
                String msg = config.getString(OUTDATED_VERSION_MSG_CONFIG);

                requestContext.abortWith(Response.status(Response.Status.CONFLICT).entity(msg).build());
            }
        }
    }

    private boolean isPartialRequest() {
        String container = request.getParameter(M_CONTAINER);
        return (container != null);
    }

}
