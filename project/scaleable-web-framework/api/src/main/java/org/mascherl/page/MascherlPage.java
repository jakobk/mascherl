package org.mascherl.page;

import java.util.HashMap;
import java.util.Map;

/**
 * The specification for a page, which should be rendered by Mascherl.
 *
 * @author Jakob Korherr
 */
public class MascherlPage {

    private String template;
    private String pageTitle;
    private final Map<String, ModelCalculator> containerModelCalculators = new HashMap<>();

    public MascherlPage() {}

    public MascherlPage template(String template) {
        this.template = template;
        return this;
    }

    public MascherlPage pageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public MascherlPage container(String containerName) {
        return container(containerName, (model) -> {});
    }

    public MascherlPage container(String containerName, ModelCalculator modelProvider) {
        containerModelCalculators.put(containerName, modelProvider);
        return this;
    }

    public void populateContainerModel(String containerName, Model pageModel) {
        ModelCalculator modelCalculator = containerModelCalculators.get(containerName);
        if (modelCalculator != null) {
            modelCalculator.populate(pageModel);
        }
    }

    public String getTemplate() {
        return template;
    }

    public String getPageTitle() {
        return pageTitle;
    }

}
