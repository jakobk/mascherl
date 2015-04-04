package org.mascherl.application;

import org.mascherl.jaxrs.ApplicationVersionRequestFilter;
import org.mascherl.jaxrs.MascherlMessageBodyWriter;
import org.mascherl.jaxrs.MascherlResponseFilter;
import org.mascherl.render.MascherlRendererFactory;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Set;

/**
 * The central object describing a Mascherl application.
 *
 * The application of Mascherl contains configuration properties (e.g. the version of the application), and
 * instances of Mascherl components (e.g. the renderer implementation), which are needed to serve Mascherl requests.
 *
 * @author Jakob Korherr
 */
public class MascherlApplication {

    private static final String MASCHERL_APPLICATION = "MASCHERL_APPLICATION";

    public static MascherlApplication getInstance(ServletContext servletContext) {
        MascherlApplication mascherlApplication = (MascherlApplication) servletContext.getAttribute(MASCHERL_APPLICATION);
        if (mascherlApplication == null) {
            throw new IllegalStateException("Mascherl is not initialized");
        }
        return mascherlApplication;
    }

    public static Set<Class<?>> getJaxRsClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ApplicationVersionRequestFilter.class);
        classes.add(MascherlMessageBodyWriter.class);
        classes.add(MascherlResponseFilter.class);
        return classes;
    }

    /**
     * Builder for MascherlContext.
     */
    public static final class Builder {

        private ServletContext servletContext;
        private MascherlRendererFactory mascherlRendererFactory;
        private ApplicationVersion applicationVersion;
        private boolean developmentMode;
        private String mascherlVersion;

        public void setMascherlRendererFactory(MascherlRendererFactory mascherlRendererFactory) {
            this.mascherlRendererFactory = mascherlRendererFactory;
        }

        public void setApplicationVersion(ApplicationVersion applicationVersion) {
            this.applicationVersion = applicationVersion;
        }

        public void setDevelopmentMode(boolean developmentMode) {
            this.developmentMode = developmentMode;
        }

        public void setMascherlVersion(String mascherlVersion) {
            this.mascherlVersion = mascherlVersion;
        }

        public MascherlApplication build(ServletContext servletContext) {
            this.servletContext = servletContext;
            MascherlApplication mascherlApplication = new MascherlApplication(this);
            servletContext.setAttribute(MASCHERL_APPLICATION, mascherlApplication);
            return mascherlApplication;
        }

    }

    private final ServletContext servletContext;
    private final MascherlRendererFactory mascherlRendererFactory;
    private final ApplicationVersion applicationVersion;
    private final boolean developmentMode;
    private final String mascherlVersion;

    private MascherlApplication(Builder builder) {
        servletContext = builder.servletContext;
        mascherlRendererFactory = builder.mascherlRendererFactory;
        applicationVersion = builder.applicationVersion;
        developmentMode = builder.developmentMode;
        mascherlVersion = builder.mascherlVersion;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public MascherlRendererFactory getMascherlRendererFactory() {
        return mascherlRendererFactory;
    }

    public ApplicationVersion getApplicationVersion() {
        return applicationVersion;
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    public String getMascherlVersion() {
        return mascherlVersion;
    }

}
