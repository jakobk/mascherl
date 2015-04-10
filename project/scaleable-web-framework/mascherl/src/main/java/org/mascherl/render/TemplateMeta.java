package org.mascherl.render;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Metadata about a template.
 *
 * @author Jakob Korherr
 */
public class TemplateMeta {

    private final String templateName;
    private final ConcurrentMap<String, ContainerMeta> containerIndex = new ConcurrentHashMap<>();

    public TemplateMeta(String templateName) {
        this.templateName = templateName;
    }

    public void addContainer(ContainerMeta containerMeta) {
        containerIndex.put(containerMeta.getContainerName(), containerMeta);
    }

    public ContainerMeta getContainerMeta(String container) {
        return containerIndex.get(container);
    }

    public String getTemplateName() {
        return templateName;
    }
}
