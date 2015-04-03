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
    private final MascherlPageSpec pageSpec;

    public FormResult(URI path) {
        this(path, null);
    }

    public FormResult(URI path, MascherlPageSpec pageSpec) {
        this.path = path;
        this.pageSpec = pageSpec;
        this.container = null;
    }

    public FormResult(String container, MascherlPageSpec pageSpec) {
        this.path = null;
        this.pageSpec = pageSpec;
        this.container = container;
    }

    public URI getPath() {
        return path;
    }

    public String getContainer() {
        return container;
    }

    public MascherlPageSpec getPageSpec() {
        return pageSpec;
    }

}
