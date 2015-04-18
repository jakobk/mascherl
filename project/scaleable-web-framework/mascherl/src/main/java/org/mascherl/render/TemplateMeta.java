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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Metadata about a template.
 *
 * @author Jakob Korherr
 */
public class TemplateMeta {

    private final String templateName;
    private final ConcurrentMap<String, ContainerMeta> containerIndex = new ConcurrentHashMap<>();

    public TemplateMeta(String templateName) {
        this.templateName = templateName;
    }

    public void addContainer(ContainerMeta containerMeta) {
        containerIndex.put(containerMeta.getContainerName(), containerMeta);
    }

    public ContainerMeta getContainerMeta(String container) {
        return containerIndex.get(container);
    }

    public String getTemplateName() {
        return templateName;
    }
}
