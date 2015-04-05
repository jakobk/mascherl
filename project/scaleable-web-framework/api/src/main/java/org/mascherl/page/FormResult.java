package org.mascherl.page;

import java.net.URI;

/**
 * A reference to a Mascherl page.
 *
 * @author Jakob Korherr
 */
public class FormResult {

    private final URI path;
    private final String container;
    private final MascherlPage mascherlPage;

    public FormResult(URI path) {
        this(path, null);
    }

    public FormResult(URI path, MascherlPage mascherlPage) {
        this(path, null, mascherlPage);
    }

    public FormResult(String container, MascherlPage mascherlPage) {
        this(null, container, mascherlPage);

    }

    public FormResult(URI path, String container, MascherlPage mascherlPage) {
        this.path = path;
        this.mascherlPage = mascherlPage;
        this.container = container;
    }

    public URI getPath() {
        return path;
    }

    public String getContainer() {
        return container;
    }

    public MascherlPage getMascherlPage() {
        return mascherlPage;
    }

}
