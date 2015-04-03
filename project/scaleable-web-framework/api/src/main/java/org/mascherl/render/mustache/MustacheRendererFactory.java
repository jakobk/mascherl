package org.mascherl.render.mustache;

import org.mascherl.application.MascherlApplication;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.render.MascherlRendererFactory;

/**
 * Mustache implementation of {@link MascherlRendererFactory}.
 *
 * @author Jakob Korherr
 */
public class MustacheRendererFactory implements MascherlRendererFactory {

    private MascherlRenderer cachedRenderer;
    private MascherlApplication application;

    @Override
    public void init(MascherlApplication application) {
        this.application = application;
        if (!application.isDevelopmentMode()) {
            cachedRenderer = new MustacheRenderer(application.getServletContext());
        }
    }

    @Override
    public MascherlRenderer createMascherlRenderer() {
        if (application.isDevelopmentMode()) {
            return new MustacheRenderer(application.getServletContext());
        } else {
            return cachedRenderer;
        }
    }

}
