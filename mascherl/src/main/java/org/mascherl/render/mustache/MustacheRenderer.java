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
package org.mascherl.render.mustache;

import com.github.mustachejava.Mustache;
import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.render.TemplateMeta;

import javax.servlet.ServletContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_PAGE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_TITLE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_URL;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_POWERED_BY;
import static org.mascherl.page.MascherlPageGroupCalculator.calculatePageGroup;

/**
 * MascherlRenderer implementation using Mustache as render engine.
 *
 * @author Jakob Korherr
 */
public class MustacheRenderer implements MascherlRenderer {

    private final MascherlMustacheFactory mustacheFactory;

    public MustacheRenderer(ServletContext servletContext) {
        mustacheFactory = new MascherlMustacheFactory(servletContext);
    }

    @Override
    public void renderFull(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                           OutputStream outputStream, MultivaluedMap<String, Object> httpHeaders) throws IOException {
        String pageGroup = calculatePageGroup(resourceInfo);
        addGeneralHttpHeaders(mascherlApplication, httpHeaders);
        render(mascherlApplication, page, pageGroup, outputStream, MAIN_CONTAINER, false);
    }

    @Override
    public void renderContainer(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                                String actionPageGroup, OutputStream outputStream, MultivaluedMap<String, Object> httpHeaders,
                                String container, String clientUrl) throws IOException {
        String pageGroup = calculatePageGroup(resourceInfo, actionPageGroup, page);
        addGeneralHttpHeaders(mascherlApplication, httpHeaders);
        addHttpHeadersForPartialResponse(page, pageGroup, httpHeaders, container, clientUrl);
        render(mascherlApplication, page, pageGroup, outputStream, container, true);
    }

    @Override
    public ContainerMeta getContainerMeta(String pageTemplate, String container) {
        TemplateMeta templateMeta = mustacheFactory.getTemplateMeta(pageTemplate);
        return templateMeta.getContainerMeta(container);
    }

    private void render(MascherlApplication mascherlApplication, MascherlPage page, String pageGroup, OutputStream outputStream,
                        String container, boolean isPartial) throws IOException {
        Mustache mustache;
        if (isPartial) {
            mustache = getContainerMustache(container, page.getTemplate());
        } else {
            mustache = mustacheFactory.compileFullPage(page.getTemplate());
        }

        MustacheRendererScope scope = new MustacheRendererScope(mascherlApplication, page, pageGroup);
        if (isPartial) {
            scope.setCurrentContainer(container);
        }

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        mustache.execute(writer, scope).flush();

        if (!isPartial && page.getReplaceUrl() != null) {
            writer.write(  // TODO jakobk: this won't work anymore (requireJS)
                    "<script>" +
                            "window.mascherl.handleHistoryChange = false;" +
                            "History.replaceState({\"container\": \"main\"}, null, \"" + page.getReplaceUrl() + "\");" +
                            "window.mascherl.handleHistoryChange = true;" +
                            "</script>\n");
            writer.flush();
        }
    }

    private void addGeneralHttpHeaders(MascherlApplication mascherlApplication, MultivaluedMap<String, Object> httpHeaders) {
        httpHeaders.putSingle(X_POWERED_BY, "Mascherl " + mascherlApplication.getMascherlVersion());
    }

    private void addHttpHeadersForPartialResponse(MascherlPage page,
                                                  String pageGroup,
                                                  MultivaluedMap<String, Object> httpHeaders,
                                                  String container, String clientUrl) {
        httpHeaders.putSingle(X_MASCHERL_TITLE, page.getPageTitle());
        httpHeaders.putSingle(X_MASCHERL_PAGE, pageGroup);
        httpHeaders.putSingle(X_MASCHERL_CONTAINER, container);
        if (clientUrl != null) {
            httpHeaders.putSingle(X_MASCHERL_URL, clientUrl);
        } else if (page.getReplaceUrl() != null) {
            httpHeaders.putSingle(X_MASCHERL_URL, page.getReplaceUrl().toString());
        }
    }

    private Mustache getContainerMustache(String container, String pageTemplate) {
        // first: make sure that the whole page template is fully compiled
        // (if so we get a cache hit in the MustacheFactory, and it won't get compiled again)
        Mustache mustache = mustacheFactory.compile(pageTemplate);
        // then: check if we should only render a sub-container, and directly use the sub-container mustache
        if (isSubContainer(container)) {
            mustache = mustacheFactory.getMustacheForContainer(pageTemplate, container);
        }
        return mustache;
    }

    private boolean isSubContainer(String container) {
        return !Objects.equals(container, MAIN_CONTAINER);
    }

}
