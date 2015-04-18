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
package org.mascherl.render;

import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Definition of a renderer for Mascherl.
 *
 * @author Jakob Korherr
 */
public interface MascherlRenderer {

    public String FULL_PAGE_RESOURCE = "/index.html";

    public void renderFull(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                           OutputStream outputStream, MultivaluedMap<String, Object> httpHeaders) throws IOException;

    public void renderContainer(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                                String actionPageGroup, OutputStream outputStream,
                                MultivaluedMap<String, Object> httpHeaders,
                                String container, String clientUrl) throws IOException;

    public ContainerMeta getContainerMeta(String pageTemplate, String container);

}
