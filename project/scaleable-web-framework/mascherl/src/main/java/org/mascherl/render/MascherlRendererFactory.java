package org.mascherl.render;

import org.mascherl.application.MascherlApplication;

/**
 * Factory for {@link MascherlRenderer}.
 *
 * An own implementation of this class can be registered via the ServiceLoader mechanism (META-INF/services),
 * and will then be picked up by the initializer of Mascherl.
 *
 * @author Jakob Korherr
 */
public interface MascherlRendererFactory {

    public void init(MascherlApplication application);

    public MascherlRenderer createMascherlRenderer();

}
