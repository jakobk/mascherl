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
import org.mascherl.page.MascherlPage;
import org.mascherl.render.MascherlRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.mascherl.MascherlConstants.RequestParameters.M_CLIENT_URL;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_PAGE;

/**
 * Implementation of {@link MessageBodyWriter} for {@link MascherlPage} entities.
 *
 * @author Jakob Korherr
 */
@Produces(MediaType.TEXT_HTML)
public class MascherlMessageBodyWriter implements MessageBodyWriter<MascherlPage> {

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MascherlPage.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(MascherlPage mascherlPage, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;  // cannot be pre-determined, because response is created while streaming
    }

    @Override
    public void writeTo(MascherlPage mascherlPage, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        MascherlApplication mascherlApplication = MascherlApplication.getInstance(request.getServletContext());

        String container = (String) request.getAttribute(M_CONTAINER);
        final boolean partialRequest = (container != null);

        MascherlRenderer renderer = mascherlApplication.getMascherlRendererFactory().createMascherlRenderer();
        if (partialRequest) {
            String clientUrl = (String) request.getAttribute(M_CLIENT_URL);
            String pageGroup = (String) request.getAttribute(M_PAGE);
            renderer.renderContainer(mascherlApplication, mascherlPage, resourceInfo, pageGroup, entityStream, httpHeaders, container, clientUrl);
        } else {
            renderer.renderFull(mascherlApplication, mascherlPage, resourceInfo, entityStream, httpHeaders);
        }
    }
}
