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
package org.mascherl.page;

import org.mascherl.application.MascherlApplication;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.servlet.MascherlFilter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.function.Supplier;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;

/**
 * Class used for defining the fluent api for controller code.
 *
 * @author Jakob Korherr
 */
public class Mascherl {

    public static MascherlAction deferredAction(Supplier<MascherlAction> supplier) {
        return new DeferredMascherlAction(supplier);
    }

    public static MascherlPage deferredPage(Supplier<MascherlPage> supplier) {
        return new DeferredMascherlPage(supplier);
    }

    public static MascherlPage page(String template) {
        HttpServletRequest request = MascherlFilter.getRequest();
        if (request != null) {
            ContainerMeta containerMeta = getContainerMeta(template, request);
            return new MascherlPage(template, containerMeta);
        } else {
            return new LazyMascherlPage(template);
        }
    }

    static ContainerMeta getContainerMeta(String template, HttpServletRequest request) {
        MascherlApplication application = MascherlApplication.getInstance(request.getServletContext());
        MascherlRenderer mascherlRenderer = application.getMascherlRendererFactory().createMascherlRenderer();
        String requestContainer = (String) request.getAttribute(M_CONTAINER);
        if (requestContainer == null) {
            requestContainer = MAIN_CONTAINER;
        }
        return mascherlRenderer.getContainerMeta(template, requestContainer);
    }

    public static NavigationHolder stay() {
        return new NavigationHolder();
    }

    public static NavigationHolder navigate(String pageUri) {
        return navigate(UriBuilder.fromUri(pageUri).build());
    }

    public static NavigationHolder navigate(URI pageUri) {
        return new NavigationHolder(pageUri);
    }

    public static class NavigationHolder {

        private final URI pageUrl;

        public NavigationHolder() {
            this(null);
        }

        public NavigationHolder(URI pageUrl) {
            this.pageUrl = pageUrl;
        }

        public ContainerHolder renderAll() {
            return renderContainer(MAIN_CONTAINER);
        }

        public ContainerHolder renderContainer(String container) {
            HttpServletRequest request = MascherlFilter.getRequest();
            if (request != null) {
                request.setAttribute(M_CONTAINER, container);
            }
            return new ContainerHolder(pageUrl, container);
        }

        public MascherlAction redirect() {
            return new MascherlAction(pageUrl);
        }

    }

    public static class ContainerHolder {

        private final URI pageUrl;
        private final String container;

        public ContainerHolder(URI pageUrl, String container) {
            this.pageUrl = pageUrl;
            this.container = container;
        }

        public MascherlAction withPageDef(MascherlPage mascherlPage) {
            return new MascherlAction(container, pageUrl, mascherlPage);
        }

    }

}
