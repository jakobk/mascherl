package org.mascherl.render.mustache.fullpage;

import com.github.mustachejava.Mustache;
import org.mascherl.render.mustache.wrapper.MustacheInterceptorWrapper;

/**
 * An interceptor-like wrapper of the full page Mustache, which sets the respective partial Mustache of the current
 * request in the {@link MainContainerPartialCode} before execution of any methods on the delegate, and removes the
 * partial again after execution
 *
 * Due to the dynamic inclusion of the respective partial Mustache, this Mustache must not be cached.
 *
 * @author Jakob Korherr
 */
public class FullPageDynamicMustache extends MustacheInterceptorWrapper {

    private final FullPageCachedMustache fullPage;
    private final Mustache partial;

    public FullPageDynamicMustache(FullPageCachedMustache fullPage, Mustache partial) {
        this.fullPage = fullPage;
        this.partial = partial;
    }

    @Override
    protected void before() {
        fullPage.getMainContainerPartialCode().getPartialThreadLocal().setMustache(partial);
    }

    @Override
    protected void after() {
        fullPage.getMainContainerPartialCode().getPartialThreadLocal().removeMustache();
    }

    @Override
    public Mustache getDelegate() {
        return fullPage;
    }

}
