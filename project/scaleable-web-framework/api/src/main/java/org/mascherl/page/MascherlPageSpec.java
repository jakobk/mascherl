package org.mascherl.page;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MascherlPageSpec {   // TODO rename to MacherlPage once we got rid of the interface

    private final String template;
    private final String pageTitle;
    private final Map<String, ModelProducer> containerModelProviders = new HashMap<>();

    public MascherlPageSpec(String template, String pageTitle) {
        this.template = template;
        this.pageTitle = pageTitle;
    }

    public MascherlPageSpec container(String containerName, ModelProducer modelProvider) {
        containerModelProviders.put(containerName, modelProvider);
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public Model getContainerModel(String containerName) {
        Model model = new Model();
        containerModelProviders.get(containerName).populate(model);
        return model;
    }

}
