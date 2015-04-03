package org.mascherl.page;

import javax.ws.rs.core.UriBuilder;

/**
 * Utility page used for defining a fluent api for controller code.
 *
 * @author Jakob Korherr
 */
public class Mascherl {

    public static MascherlPageSpec page() {
        return new MascherlPageSpec();
    }

    public static FormResult redirect(String pageUri) {
        return new FormResult(UriBuilder.fromUri(pageUri).build());
    }

    public static FormResult renderContainer(String container, MascherlPageSpec pageSpec) {
        return new FormResult(container, pageSpec);
    }

    public static FormResult renderPage(String pageUri, MascherlPageSpec pageSpec) {
        return new FormResult(UriBuilder.fromUri(pageUri).build(), pageSpec);
    }

}
