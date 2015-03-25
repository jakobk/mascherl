package org.mascherl.page;

/**
 * A container reference.
 *
 * @author Jakob Korherr
 */
public class ContainerRef {

    private final String container;

    public ContainerRef(String container) {
        if (container == null) {
            throw new IllegalArgumentException("container cannot be null");
        }
        this.container = container;
    }

    public String getContainer() {
        return container;
    }
}
