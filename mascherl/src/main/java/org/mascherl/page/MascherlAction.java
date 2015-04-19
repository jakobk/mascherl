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
package org.mascherl.page;

import java.net.URI;

/**
 * An action outcome of a resource method.
 *
 * @author Jakob Korherr
 */
public class MascherlAction {

    private final URI pageUrl;
    private final String container;
    private final MascherlPage mascherlPage;
    private String pageGroup;

    MascherlAction() {
        this(null);
    }

    MascherlAction(URI pageUrl) {
        this(null, pageUrl, null);
    }

    MascherlAction(String container, URI pageUrl, MascherlPage mascherlPage) {
        this.container = container;
        this.pageUrl = pageUrl;
        this.mascherlPage = mascherlPage;
    }

    public MascherlAction withPageGroup(String pageGroup) {
        this.pageGroup = pageGroup;
        return this;
    }

    public String getPageGroup() {
        return pageGroup;
    }

    public URI getPageUrl() {
        return pageUrl;
    }

    public String getContainer() {
        return container;
    }

    public MascherlPage getMascherlPage() {
        return mascherlPage;
    }

}
