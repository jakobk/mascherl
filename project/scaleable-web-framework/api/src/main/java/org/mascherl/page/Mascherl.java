package org.mascherl.page;

import javax.ws.rs.core.UriBuilder;

/**
 * Utility page used for defining a fluent api for controller code.
 *
 * @author Jakob Korherr
 */
public class Mascherl {

    public static MascherlPage page() {
        return new MascherlPage();
    }

    public static FormResult redirect(String pageUri) {
        return new FormResult(UriBuilder.fromUri(pageUri).build());
    }

    public static FormResult renderContainer(String container, MascherlPage mascherlPage) {
        return new FormResult(container, mascherlPage);
    }

    public static FormResult renderPage(String pageUri, MascherlPage mascherlPage) {
        return new FormResult(UriBuilder.fromUri(pageUri).build(), mascherlPage);
    }

}
