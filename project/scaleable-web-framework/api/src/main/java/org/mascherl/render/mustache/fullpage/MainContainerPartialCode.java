package org.mascherl.render.mustache.fullpage;

import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.PartialCode;
import org.mascherl.MascherlConstants;
import org.mascherl.render.mustache.MascherlMustacheFactory;
import org.mascherl.render.mustache.wrapper.ThreadLocalMustacheDelegate;

/**
 * Special {@link PartialCode}, which includes the current pageTemplate instead of a static resource.
 *
 * @author Jakob Korherr
 */
public class MainContainerPartialCode extends PartialCode {

    private final ThreadLocalMustacheDelegate partialThreadLocal = new ThreadLocalMustacheDelegate();

    public MainContainerPartialCode(TemplateContext tc, MascherlMustacheFactory cf) {
        super(tc, cf, MascherlConstants.MAIN_CONTAINER);
    }

    @Override
    public synchronized void init() {
        filterText();
        partial = partialThreadLocal;
    }

    public ThreadLocalMustacheDelegate getPartialThreadLocal() {
        return partialThreadLocal;
    }

}
