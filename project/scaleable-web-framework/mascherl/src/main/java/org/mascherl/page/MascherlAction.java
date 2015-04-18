package org.mascherl.page;

import java.net.URI;

/**
 * An action outcome of a resource method.
 *
 * @author Jakob Korherr
 */
public class MascherlAction {

    private final URI pageUrl;
    private final String container;
    private final MascherlPage mascherlPage;
    private String pageGroup;

    MascherlAction() {
        this(null);
    }

    MascherlAction(URI pageUrl) {
        this(null, pageUrl, null);
    }

    MascherlAction(String container, URI pageUrl, MascherlPage mascherlPage) {
        this.container = container;
        this.pageUrl = pageUrl;
        this.mascherlPage = mascherlPage;
    }

    public MascherlAction withPageGroup(String pageGroup) {
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
