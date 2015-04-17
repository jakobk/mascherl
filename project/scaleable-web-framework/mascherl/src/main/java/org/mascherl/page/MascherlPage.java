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

    private String pageTitle;

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
}
