/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.application;

import org.mascherl.jaxrs.MascherlMessageBodyWriter;
import org.mascherl.jaxrs.MascherlRequestFilter;
import org.mascherl.jaxrs.MascherlResponseFilter;
import org.mascherl.render.MascherlRendererFactory;
import org.mascherl.session.MascherlSessionStorage;
import org.mascherl.version.ApplicationVersion;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Set;

import static org.mascherl.MascherlConstants.MASCHERL_APPLICATION_CONTEXT_ATTRIBUTE;

/**
 * The central object describing a Mascherl application.
 *
 * The application of Mascherl contains configuration properties (e.g. the version of the application), and
 * instances of Mascherl components (e.g. the renderer implementation), which are needed to serve Mascherl requests.
 *
 * @author Jakob Korherr
 */
public class MascherlApplication {

    public static MascherlApplication getInstance(ServletContext servletContext) {
        MascherlApplication mascherlApplication = (MascherlApplication) servletContext.getAttribute(MASCHERL_APPLICATION_CONTEXT_ATTRIBUTE);
        if (mascherlApplication == null) {
            throw new IllegalStateException("Mascherl is not initialized");
        }
        return mascherlApplication;
    }

    public static Set<Class<?>> getJaxRsClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(MascherlRequestFilter.class);
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
        private MascherlSessionStorage mascherlSessionStorage;
        private ApplicationVersion applicationVersion;
        private boolean developmentMode;
        private String mascherlVersion;

        public void setMascherlRendererFactory(MascherlRendererFactory mascherlRendererFactory) {
            this.mascherlRendererFactory = mascherlRendererFactory;
        }

        public void setMascherlSessionStorage(MascherlSessionStorage mascherlSessionStorage) {
            this.mascherlSessionStorage = mascherlSessionStorage;
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
            servletContext.setAttribute(MASCHERL_APPLICATION_CONTEXT_ATTRIBUTE, mascherlApplication);
            return mascherlApplication;
        }

    }

    private final ServletContext servletContext;
    private final MascherlRendererFactory mascherlRendererFactory;
    private final MascherlSessionStorage mascherlSessionStorage;
    private final ApplicationVersion applicationVersion;
    private final boolean developmentMode;
    private final String mascherlVersion;

    private MascherlApplication(Builder builder) {
        servletContext = builder.servletContext;
        mascherlRendererFactory = builder.mascherlRendererFactory;
        mascherlSessionStorage = builder.mascherlSessionStorage;
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

    public MascherlSessionStorage getMascherlSessionStorage() {
        return mascherlSessionStorage;
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
