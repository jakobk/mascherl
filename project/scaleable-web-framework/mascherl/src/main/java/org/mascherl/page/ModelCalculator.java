package org.mascherl.page;

/**
 * Specification for populating a {@link Model}.
 *
 * @author Jakob Korherr
 */
@FunctionalInterface
public interface ModelCalculator {

    public void populate(Model model);

}
