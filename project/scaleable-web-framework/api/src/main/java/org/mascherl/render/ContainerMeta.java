package org.mascherl.render;

import java.util.LinkedList;
import java.util.List;

/**
 * Meta information about a container.
 *
 * @author Jakob Korherr
 */
public class ContainerMeta {

    private final String containerName;
    private ContainerMeta parent;
    private final List<ContainerMeta> children = new LinkedList<>();

    public ContainerMeta(String containerName) {
        this.containerName = containerName;
    }

    public void addChild(ContainerMeta child) {
        children.add(child);
        child.parent = this;
    }

    public ContainerMeta getParent() {
        return parent;
    }

    public List<ContainerMeta> getChildren() {
        return children;
    }

    public String getContainerName() {
        return containerName;
    }
}
