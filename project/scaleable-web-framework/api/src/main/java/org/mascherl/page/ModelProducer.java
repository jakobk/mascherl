package org.mascherl.page;

/**
 * Specification of a producer of a {@link Model}
 *
 * @author Jakob Korherr
 */
@FunctionalInterface
public interface ModelProducer {

    public void populate(Model model);

}
