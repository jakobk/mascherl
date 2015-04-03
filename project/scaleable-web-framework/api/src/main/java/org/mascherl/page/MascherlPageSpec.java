package org.mascherl.page;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MascherlPageSpec {   // TODO rename to MacherlPage once we got rid of the interface

    private String template;
    private String pageTitle;
    private final Map<String, ModelCalculator> containerModelCalculators = new HashMap<>();
    private String pageId;   // TODO find a better solution to store the pageId

    public MascherlPageSpec() {}

    public MascherlPageSpec template(String template) {
        this.template = template;
        return this;
    }

    public MascherlPageSpec pageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public MascherlPageSpec container(String containerName) {
        return container(containerName, (model) -> {});
    }

    public MascherlPageSpec container(String containerName, ModelCalculator modelProvider) {
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

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }


}
