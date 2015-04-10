package org.mascherl.jaxrs;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Objects;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_APP_VERSION;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_PAGE;
import static org.mascherl.page.MascherlPageGroupCalculator.calculatePageGroup;

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

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MascherlApplication mascherlApplication = MascherlApplication.getInstance(servletContext);

        verifyApplicationVersion(mascherlApplication, requestContext);

        restoreSession(mascherlApplication);

        calculateRequestContainer(mascherlApplication);
    }

    private void calculateRequestContainer(MascherlApplication mascherlApplication) {
        if (MascherlPage.class.isAssignableFrom(resourceInfo.getResourceMethod().getReturnType())) {
            String container = (String) request.getAttribute(M_CONTAINER);
            if (container == null) {
                container = request.getParameter(M_CONTAINER);
            }
            if (container != null) {
                String requestPageGroup = request.getParameter(M_PAGE);
                String resourcePageGroup = calculatePageGroup(mascherlApplication, resourceInfo);
                if (requestPageGroup != null && !Objects.equals(requestPageGroup, resourcePageGroup)) {
                    container = MAIN_CONTAINER;
                }
            }
            request.setAttribute(M_CONTAINER, container);
        }
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
