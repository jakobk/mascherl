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

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Model} specifies the data which is needed to render a specific container on a page.
 *
 * It contains all data which is needed to render the related container template (push style).
 *
 * @author Jakob Korherr
 */
public class Model {

    private final Map<String, Object> scope = new HashMap<>();

    public Model() {
    }

    public Model put(String key, Object value) {
        scope.put(key, value);
        return this;
    }

    public Map<String, Object> getScope() {
        return scope;
    }
}
