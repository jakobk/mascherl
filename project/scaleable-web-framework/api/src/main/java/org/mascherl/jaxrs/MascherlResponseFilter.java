package org.mascherl.jaxrs;

import org.mascherl.application.MascherlApplication;
import org.mascherl.page.FormResult;
import org.mascherl.servlet.MascherlFilter;
import org.mascherl.session.MascherlSession;
import org.mascherl.session.MascherlSessionStorage;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_APP_VERSION;
import static org.mascherl.MascherlConstants.RequestParameters.M_CLIENT_URL;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;

/**
 * Implementation of {@link javax.ws.rs.container.ContainerResponseFilter} for executing Mascherl specific tasks
 * after a resource method has been called.
 *
 * @author Jakob Korherr
 */
public class MascherlResponseFilter implements ContainerResponseFilter {

    @Context
    private ServletContext servletContext;

    @Context
    private ResourceContext resourceContext;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MascherlApplication mascherlApplication = MascherlApplication.getInstance(servletContext);
        HttpServletRequest request = MascherlFilter.getRequest();
        HttpServletResponse response = MascherlFilter.getResponse();

        saveSession(mascherlApplication, response);

        handleFormResultEntity(responseContext, mascherlApplication, request, response);
    }

    private void saveSession(MascherlApplication mascherlApplication, HttpServletResponse response) {
        MascherlSessionStorage sessionStorage = mascherlApplication.getMascherlSessionStorage();
        sessionStorage.saveSession(MascherlSession.getInstance(), response);
    }

    private void handleFormResultEntity(ContainerResponseContext responseContext, MascherlApplication mascherlApplication,
                                        HttpServletRequest request, HttpServletResponse response) {
        if (responseContext.getEntity() instanceof FormResult) {
            FormResult formResult = (FormResult) responseContext.getEntity();

            if (formResult.getPageUrl() != null) {
                String clientUrl = formResult.getPageUrl().toString();
                request.setAttribute(M_CLIENT_URL, clientUrl);

                if (formResult.getMascherlPage() != null) {
                    String container = formResult.getContainer();
                    if (container == null) {
                        container = MAIN_CONTAINER;
                    }

                    request.setAttribute(M_CONTAINER, container);
                    responseContext.setEntity(formResult.getMascherlPage());
                } else {
                    redirect(mascherlApplication, responseContext, formResult.getPageUrl(), request, response);
                }
            } else if (formResult.getContainer() != null) {
                request.setAttribute(M_CONTAINER, formResult.getContainer());
                responseContext.setEntity(formResult.getMascherlPage());
            } else {
                throw new IllegalArgumentException("Illegal FormResult " + formResult);
            }
        }
    }

    private void redirect(MascherlApplication mascherlApplication, ContainerResponseContext responseContext,
                          URI path, HttpServletRequest request, HttpServletResponse response) {
        UriBuilder uriBuilder = UriBuilder.fromUri(path);
        uriBuilder.queryParam(M_CONTAINER, MAIN_CONTAINER);

        ApplicationVersion applicationVersion = mascherlApplication.getApplicationVersion();
        uriBuilder.queryParam(M_APP_VERSION, applicationVersion.getVersion());

        responseContext.setEntity(null);
        forwardAsGetRequest(request, response, uriBuilder.build());
    }

    private static void forwardAsGetRequest(HttpServletRequest request, HttpServletResponse response, URI uri) {
        try {
            request.getServletContext().getRequestDispatcher(uri.toString())
                    .forward(new DispatcherForwardServletRequest(request), response);
        } catch (ServletException | IOException e) {
            throw new WebApplicationException(e);
        }
    }

    /**
     * Servlet request wrapper for a call to requestDispatcher.forward(), emulating a GET request.
     *
     * @author Jakob Korherr
     */
    private static class DispatcherForwardServletRequest extends HttpServletRequestWrapper {

        public DispatcherForwardServletRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getMethod() {
            return "GET";  // emulate GET request
        }

    }
}
