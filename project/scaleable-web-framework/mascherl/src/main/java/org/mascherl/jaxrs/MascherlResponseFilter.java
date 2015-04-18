/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.jaxrs;

import org.mascherl.application.MascherlApplication;
import org.mascherl.page.LazyMascherlPage;
import org.mascherl.page.MascherlAction;
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
import static org.mascherl.MascherlConstants.RequestParameters.M_PAGE;

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

        evaluateLazyMascherlPage(responseContext, request);

        saveSession(mascherlApplication, response);

        handleMascherlActionEntity(responseContext, mascherlApplication, request, response);
    }

    private void evaluateLazyMascherlPage(ContainerResponseContext responseContext, HttpServletRequest request) {
        if (responseContext.getEntity() instanceof LazyMascherlPage) {
            LazyMascherlPage lazyMascherlPage = (LazyMascherlPage) responseContext.getEntity();
            lazyMascherlPage.build(request);
        } else if (responseContext.getEntity() instanceof MascherlAction) {
            MascherlAction mascherlAction = (MascherlAction) responseContext.getEntity();
            if (mascherlAction.getMascherlPage() instanceof LazyMascherlPage) {
                LazyMascherlPage lazyMascherlPage = (LazyMascherlPage) responseContext.getEntity();
                lazyMascherlPage.build(request);
            }
        }
    }

    private void saveSession(MascherlApplication mascherlApplication, HttpServletResponse response) {
        MascherlSessionStorage sessionStorage = mascherlApplication.getMascherlSessionStorage();
        sessionStorage.saveSession(MascherlSession.getInstance(), response);
    }

    private void handleMascherlActionEntity(ContainerResponseContext responseContext, MascherlApplication mascherlApplication,
                                            HttpServletRequest request, HttpServletResponse response) {
        if (responseContext.getEntity() instanceof MascherlAction) {
            MascherlAction mascherlAction = (MascherlAction) responseContext.getEntity();

            if (mascherlAction.getPageUrl() != null) {
                String clientUrl = mascherlAction.getPageUrl().toString();
                request.setAttribute(M_CLIENT_URL, clientUrl);

                if (mascherlAction.getMascherlPage() != null) {
                    String container = mascherlAction.getContainer();
                    if (container == null) {
                        container = MAIN_CONTAINER;
                    }

                    request.setAttribute(M_CONTAINER, container);
                    request.setAttribute(M_PAGE, mascherlAction.getPageGroup());
                    responseContext.setEntity(mascherlAction.getMascherlPage());
                } else {
                    redirect(mascherlApplication, responseContext, mascherlAction.getPageUrl(), request, response);
                }
            } else if (mascherlAction.getContainer() != null) {
                request.setAttribute(M_CONTAINER, mascherlAction.getContainer());
                request.setAttribute(M_PAGE, mascherlAction.getPageGroup());
                responseContext.setEntity(mascherlAction.getMascherlPage());
            } else {
                throw new IllegalArgumentException("Illegal " + MascherlAction.class.getSimpleName() + ": " + mascherlAction);
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
