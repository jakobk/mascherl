package org.mascherl.jaxrs;

import org.apache.cxf.jaxrs.impl.tl.ThreadLocalProxy;
import org.mascherl.application.MascherlApplication;
import org.mascherl.page.FormResult;
import org.mascherl.version.ApplicationVersion;

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
 * ContainerResponseFilter for implementing {@link org.mascherl.page.FormResult} results.
 *
 * @author Jakob Korherr
 */
public class MascherlResponseFilter implements ContainerResponseFilter {

    @Context
    private HttpServletRequest threadLocalRequest;

    @Context
    private HttpServletResponse threadLocalResponse;

    @Context
    private ResourceContext resourceContext;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getEntity() instanceof FormResult) {
            FormResult formResult = (FormResult) responseContext.getEntity();

            // TODO find something CXF independent here, e.g. ServletFilter
            @SuppressWarnings("unchecked")
            HttpServletRequest request = ((ThreadLocalProxy<HttpServletRequest>) threadLocalRequest).get();
            @SuppressWarnings("unchecked")
            HttpServletResponse response = ((ThreadLocalProxy<HttpServletResponse>) threadLocalResponse).get();

            if (formResult.getPath() != null) {
                String clientUrl = formResult.getPath().toString();
                request.setAttribute(M_CLIENT_URL, clientUrl);

                if (formResult.getPageSpec() != null) {
                    request.setAttribute(M_CONTAINER, MAIN_CONTAINER);
                    responseContext.setEntity(formResult.getPageSpec());
                } else {
                    redirect(responseContext, formResult.getPath(), request, response);
                }
            } else if (formResult.getContainer() != null) {
                request.setAttribute(M_CONTAINER, formResult.getContainer());
                responseContext.setEntity(formResult.getPageSpec());
            } else {
                throw new IllegalArgumentException("container and path of FormResult cannot both be null");
            }
        }
    }

    private void redirect(ContainerResponseContext responseContext, URI path, HttpServletRequest request, HttpServletResponse response) {
        UriBuilder uriBuilder = UriBuilder.fromUri(path);
        uriBuilder.queryParam(M_CONTAINER, MAIN_CONTAINER);

        ApplicationVersion applicationVersion = MascherlApplication.getInstance(request.getServletContext()).getApplicationVersion();
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
