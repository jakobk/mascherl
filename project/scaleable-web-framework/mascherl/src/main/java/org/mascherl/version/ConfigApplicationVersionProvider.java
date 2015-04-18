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
package org.mascherl.version;

import com.typesafe.config.ConfigFactory;

/**
 * Default implementation of {@link ApplicationVersionProvider}, which uses typesafe's config to
 * calculate the current application version.
 *
 * @author Jakob Korherr
 */
public class ConfigApplicationVersionProvider implements ApplicationVersionProvider {

    private static final String APPLICATION_VERSION_CONFIG_KEY = "org.mascherl.version.applicationVersion";

    @Override
    public ApplicationVersion getApplicationVersion() {
        String version = ConfigFactory.load().getString(APPLICATION_VERSION_CONFIG_KEY);
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Mascherl: application version must not be null or empty.");
        }
        return new ApplicationVersion(version);
    }

}
