package org.mascherl.page;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * A reference to a Mascherl page.
 *
 * @author Jakob Korherr
 */
public class FormResult {

    private URI pageUrl;
    private final String container;
    private final MascherlPage mascherlPage;

    FormResult(URI pageUrl) {
        this(null, null);
        this.pageUrl = pageUrl;
    }

    FormResult(String container, MascherlPage mascherlPage) {
        this.mascherlPage = mascherlPage;
        this.container = container;
    }

    public FormResult useUrl(String pageUrl) {
        return useUrl(UriBuilder.fromUri(pageUrl).build());
    }

    public FormResult useUrl(URI pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public URI getPageUrl() {
        return pageUrl;
    }

    public String getContainer() {
        return container;
    }

    public MascherlPage getMascherlPage() {
        return mascherlPage;
    }

}
