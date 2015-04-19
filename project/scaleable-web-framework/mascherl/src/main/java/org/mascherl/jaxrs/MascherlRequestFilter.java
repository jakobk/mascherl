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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mascherl.MascherlConstants;
import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;
import org.mascherl.version.ApplicationVersion;

import javax.annotation.Priority;
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
import static org.mascherl.MascherlConstants.RequestHeaders.X_MASCHERL_APP_VERSION;
import static org.mascherl.MascherlConstants.RequestHeaders.X_MASCHERL_CONTAINER;
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
@Priority(MascherlConstants.FILTER_PRIORITY)
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

        calculateRequestContainer();
    }

    private void calculateRequestContainer() {
        String container = (String) request.getAttribute(M_CONTAINER);
        if (container == null) {
            container = request.getParameter(M_CONTAINER);
            if (container == null) {
                container = request.getHeader(X_MASCHERL_CONTAINER);
            }
        }
        if (container != null) {
            String requestPageGroup = request.getParameter(M_PAGE);
            String resourcePageGroup = calculatePageGroup(resourceInfo);
            if (requestPageGroup != null && !Objects.equals(requestPageGroup, resourcePageGroup)) {
                container = MAIN_CONTAINER;
            }
        }
        request.setAttribute(M_CONTAINER, container);
    }

    private void restoreSession(MascherlApplication mascherlApplication) {
        mascherlApplication.getMascherlSessionStorage().restoreSession(request);
    }

    private void verifyApplicationVersion(MascherlApplication mascherlApplication, ContainerRequestContext requestContext) {
        if (isPartialRequest()) {
            ApplicationVersion clientAppVersion = getRequestApplicationVersion(requestContext);
            if (!mascherlApplication.getApplicationVersion().equals(clientAppVersion)) {
                Config config = ConfigFactory.load();
                String msg = config.getString(OUTDATED_VERSION_MSG_CONFIG);

                requestContext.abortWith(Response.status(Response.Status.CONFLICT).entity(msg).build());
            }
        }
    }

    private ApplicationVersion getRequestApplicationVersion(ContainerRequestContext requestContext) {
        String version;
        if (Objects.equals("GET", request.getMethod())) {
            version = requestContext.getUriInfo().getQueryParameters().getFirst(M_APP_VERSION);
        } else {
            version = request.getHeader(X_MASCHERL_APP_VERSION);
        }
        return new ApplicationVersion(version);
    }

    private boolean isPartialRequest() {
        String container;
        if (Objects.equals("GET", request.getMethod())) {
            container = request.getParameter(M_CONTAINER);
        } else {
            container = request.getHeader(X_MASCHERL_CONTAINER);
        }
        return (container != null);
    }

}
