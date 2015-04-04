package org.mascherl.render.mustache.fullpage;

import com.github.mustachejava.Code;
import com.github.mustachejava.Mustache;
import org.mascherl.render.mustache.wrapper.MustacheInterceptorWrapper;

/**
 * A wrapper for the Mustache of the full page template, which looks up the {@link MainContainerPartialCode} in the
 * code tree and stores a reference to it. This reference is later needed to include the correct partial Mustache into
 * the main container.
 *
 * Because this Mustache will not change, it can be cached in the mustache cache of the factory.
 *
 * @author Jakob Korherr
 */
public class FullPageCachedMustache extends MustacheInterceptorWrapper {

    private final Mustache fullPage;
    private final MainContainerPartialCode mainContainerPartialCode;

    public FullPageCachedMustache(Mustache fullPage) {
        this.fullPage = fullPage;
        mainContainerPartialCode = findMainContainerPartialCode(fullPage);
        if (mainContainerPartialCode == null) {
            throw new IllegalStateException("Full page template does not include {{$main}}{{/main}}");
        }
    }

    private static MainContainerPartialCode findMainContainerPartialCode(Code start) {
        if (start != null && start.getCodes() != null) {
            for (Code code : start.getCodes()) {
                if (code instanceof MainContainerPartialCode) {
                    return (MainContainerPartialCode) code;
                } else {
                    MainContainerPartialCode recursiveResult = findMainContainerPartialCode(code);
                    if (recursiveResult != null) {
                        return recursiveResult;
                    }
                }
            }
        }
        return null;
    }

    public MainContainerPartialCode getMainContainerPartialCode() {
        return mainContainerPartialCode;
    }

    @Override
    public Mustache getDelegate() {
        return fullPage;
    }

}
