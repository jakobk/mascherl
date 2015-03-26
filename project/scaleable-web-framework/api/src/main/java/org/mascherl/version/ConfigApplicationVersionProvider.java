package org.mascherl.version;

import com.typesafe.config.ConfigFactory;

/**
 * Default implementation of {@link ApplicationVersionProvider}, which uses typesafe's config to
 * calculate the current application version.
 *
 * @author Jakob Korherr
 */
public class ConfigApplicationVersionProvider implements ApplicationVersionProvider {

    @Override
    public ApplicationVersion getApplicationVersion() {
        String version = ConfigFactory.load().getString(ApplicationVersion.class.getName());
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Mascherl: application version must not be null or empty.");
        }
        return new ApplicationVersion(version);
    }

}
