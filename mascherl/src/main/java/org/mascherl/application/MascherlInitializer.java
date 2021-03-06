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

import com.typesafe.config.ConfigFactory;
import org.mascherl.MascherlConstants;
import org.mascherl.render.MascherlRendererFactory;
import org.mascherl.render.mustache.MustacheRendererFactory;
import org.mascherl.session.MascherlSessionStorage;
import org.mascherl.version.ApplicationVersionProvider;
import org.mascherl.version.ConfigApplicationVersionProvider;

import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Initializes Mascherl by creating and initializing {@link org.mascherl.application.MascherlApplication}.
 *
 * @author Jakob Korherr
 */
public class MascherlInitializer {

    private static final Logger logger = Logger.getLogger(MascherlInitializer.class.getName());

    private static final String DEVELOPMENT_MODUS = "org.mascherl.developmentModus";

    private final ServletContext servletContext;

    public MascherlInitializer(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void initialize() {
        MascherlApplication.Builder builder = new MascherlApplication.Builder();
        builder.setMascherlRendererFactory(getMustacheRendererFactory());
        builder.setMascherlSessionStorage(new MascherlSessionStorage());
        builder.setApplicationVersion(getApplicationVersionProvider().getApplicationVersion());
        builder.setDevelopmentMode(ConfigFactory.load().getBoolean(DEVELOPMENT_MODUS));
        builder.setMascherlVersion(findMascherlVersion());
        MascherlApplication application = builder.build(servletContext);

        if (application.isDevelopmentMode()) {
            logger.info("\n" +
                    "############################################################\n" +
                    "## Mascherl is running in DEVELOPMENT MODE.               ##\n" +
                    "## Do not deploy to your production servers in this mode! ##\n" +
                    "############################################################");
        }

        application.getMascherlRendererFactory().init(application);
        application.getMascherlSessionStorage().init(application);
    }

    private String findMascherlVersion() {
        String version = MascherlConstants.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "dev-snapshot-" + LocalDateTime.now().toString();
        }
        return version;
    }

    private MascherlRendererFactory getMustacheRendererFactory () {
        Iterator<MascherlRendererFactory > providers = ServiceLoader.load(MascherlRendererFactory.class).iterator();
        if (providers.hasNext()) {
            return providers.next();
        }
        return new MustacheRendererFactory();   // default impl
    }

    private ApplicationVersionProvider getApplicationVersionProvider() {
        Iterator<ApplicationVersionProvider> providers = ServiceLoader.load(ApplicationVersionProvider.class).iterator();
        if (providers.hasNext()) {
            return providers.next();
        }
        return new ConfigApplicationVersionProvider();   // default impl
    }

}
