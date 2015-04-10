package org.mascherl.page;

import java.net.URI;

/**
 * A reference to a Mascherl page.
 *
 * @author Jakob Korherr
 */
public class FormResult {

    private final URI pageUrl;
    private final String container;
    private final MascherlPage mascherlPage;
    private String pageGroup;

    FormResult(URI pageUrl) {
        this(null, pageUrl, null);
    }

    FormResult(String container, URI pageUrl, MascherlPage mascherlPage) {
        this.container = container;
        this.pageUrl = pageUrl;
        this.mascherlPage = mascherlPage;
    }

    public FormResult withPageGroup(String pageGroup) {
        this.pageGroup = pageGroup;
        return this;
    }

    public String getPageGroup() {
        return pageGroup;
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
