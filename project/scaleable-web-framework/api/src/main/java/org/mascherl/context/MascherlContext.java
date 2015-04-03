package org.mascherl.context;

import org.mascherl.render.MascherlRenderer;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.ServletContext;
import java.util.Set;

/**
 * The singleton context of Mascherl (one instance for the whole web application).
 *
 * Stores metadata and application instances needed for the processing of requests.
 *
 * @author Jakob Korherr
 */
public class MascherlContext {

    private static final String MASCHERL_CONTEXT = "MASCHERL_CONTEXT";

    public static MascherlContext getInstance(ServletContext servletContext) {
        MascherlContext mascherlContext = (MascherlContext) servletContext.getAttribute(MASCHERL_CONTEXT);
        if (mascherlContext == null) {
            throw new IllegalStateException("Mascherl is not initialized");
        }
        return mascherlContext;
    }

    /**
     * Builder for MascherlContext.
     */
    public static final class Builder {

        private Set<Class<?>> pageClasses;
        private MascherlRenderer mascherlRenderer;
        private ApplicationVersion applicationVersion;

        public void setMascherlRenderer(MascherlRenderer mascherlRenderer) {
            this.mascherlRenderer = mascherlRenderer;
        }

        public void setApplicationVersion(ApplicationVersion applicationVersion) {
            this.applicationVersion = applicationVersion;
        }

        public void setPageClasses(Set<Class<?>> pageClasses) {
            this.pageClasses = pageClasses;
        }

        public MascherlContext build(ServletContext servletContext) {
            MascherlContext mascherlContext = new MascherlContext(this);
            servletContext.setAttribute(MASCHERL_CONTEXT, mascherlContext);
            return mascherlContext;
        }

    }

    // MascherlContext is shared by all threads, thus must be thread-safe
    private final Set<Class<?>> pageClasses;
    private final MascherlRenderer mascherlRenderer;
    private final ApplicationVersion applicationVersion;

    private MascherlContext(Builder builder) {
        pageClasses = builder.pageClasses;
        mascherlRenderer = builder.mascherlRenderer;
        applicationVersion = builder.applicationVersion;
    }

    public MascherlRenderer getMascherlRenderer() {
        return mascherlRenderer;
    }

    public ApplicationVersion getApplicationVersion() {
        return applicationVersion;
    }

    public Set<Class<?>> getPageClasses() {
        return pageClasses;
    }

}
