package org.mascherl.page;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Utility class used for defining a fluent api for controller code.
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

    public static FormResult renderPage(URI pageUri, MascherlPage mascherlPage) {
        return new FormResult(pageUri, mascherlPage);
    }

    public static FormResult renderContainerOfPage(String container, String pageUri, MascherlPage mascherlPage) {
        return new FormResult(UriBuilder.fromUri(pageUri).build(), container, mascherlPage);
    }

    public static FormResult renderContainerOfPage(String container, URI pageUri, MascherlPage mascherlPage) {
        return new FormResult(pageUri, container, mascherlPage);
    }

}
