package org.mascherl.page;

import java.net.URI;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A {@link MascherlPage} proxy, which creates the actual {@link MascherlPage} and delegates to it at first access
 * of any of the getters of {@link MascherlPage}.
 *
 * Necessary, if the creation of the actual MascherlPage requires a certain thread, which is not available right now,
 * e.g. in asynchronous request processing, if the creation of the MascherlPage needs to run on a container thread
 * with the container context objects in place (e.g. HttpServletRequest, HttpServletResponse, etc).
 *
 * @see org.mascherl.page.Mascherl#deferredPage(java.util.function.Supplier)
 *
 * @author Jakob Korherr
 */
public class DeferredMascherlPage extends MascherlPage {

    private final Supplier<MascherlPage> supplier;
    private MascherlPage mascherlPage;

    public DeferredMascherlPage(Supplier<MascherlPage> supplier) {
        this.supplier = supplier;
    }

    private MascherlPage getDelegate() {
        if (mascherlPage == null) {
            mascherlPage = supplier.get();
        }
        return mascherlPage;
    }

    @Override
    public String getTemplate() {
        return getDelegate().getTemplate();
    }

    @Override
    public String getPageTitle() {
        return getDelegate().getPageTitle();
    }

    @Override
    public Map<String, Model> getContainerModels() {
        return getDelegate().getContainerModels();
    }

    @Override
    public URI getReplaceUrl() {
        return getDelegate().getReplaceUrl();
    }

    @Override
    public MascherlPage pageTitle(String pageTitle) {
        throw new UnsupportedOperationException("Not supported on deferred implementation");
    }

    @Override
    public MascherlPage container(String containerName) {
        throw new UnsupportedOperationException("Not supported on deferred implementation");
    }

    @Override
    public MascherlPage container(String containerName, ModelCalculator modelCalculator) {
        throw new UnsupportedOperationException("Not supported on deferred implementation");
    }

    @Override
    public MascherlPage replaceUrl(String url) {
        throw new UnsupportedOperationException("Not supported on deferred implementation");
    }

    @Override
    public MascherlPage replaceUrl(URI newUrl) {
        throw new UnsupportedOperationException("Not supported on deferred implementation");
    }
}
