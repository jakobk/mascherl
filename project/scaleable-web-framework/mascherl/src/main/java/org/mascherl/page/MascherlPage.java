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

import org.mascherl.render.ContainerMeta;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The specification for a page, which should be rendered by Mascherl.
 *
 * @author Jakob Korherr
 */
public class MascherlPage {

    private final String template;
    private final Set<String> containersToEvaluate = new HashSet<>();
    private final Map<String, Model> containerModels = new HashMap<>();
    private URI replaceUrl;
    private String pageGroup;

    private String pageTitle;

    MascherlPage() {
        this(null);
    }

    MascherlPage(String template) {
        this.template = template;
    }

    MascherlPage(String template, ContainerMeta requestContainerMeta) {
        this.template = template;
        findContainersToEvaluate(requestContainerMeta);
    }

    protected void findContainersToEvaluate(ContainerMeta containerMeta) {
        containersToEvaluate.add(containerMeta.getContainerName());
        containerMeta.getChildren().forEach(this::findContainersToEvaluate);
    }

    public MascherlPage pageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public MascherlPage container(String containerName) {
        return container(containerName, (model) -> {});
    }

    public MascherlPage container(String containerName, ModelCalculator modelCalculator) {
        if (containersToEvaluate.contains(containerName)) {
            Model model = containerModels.get(containerName);  // look for existing model --> model override
            if (model == null) {
                model = new Model();
                containerModels.put(containerName, model);
            }
            modelCalculator.populate(model);
        }
        return this;
    }

    public MascherlPage replaceUrl(String url) {
        return replaceUrl(UriBuilder.fromUri(url).build());
    }

    public MascherlPage replaceUrl(URI newUrl) {
        this.replaceUrl = newUrl;
        return this;
    }

    public MascherlPage pageGroup(String pageGroup) {
        this.pageGroup = pageGroup;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public Map<String, Model> getContainerModels() {
        return containerModels;
    }

    public URI getReplaceUrl() {
        return replaceUrl;
    }

    public String getPageGroup() {
        return pageGroup;
    }
}
