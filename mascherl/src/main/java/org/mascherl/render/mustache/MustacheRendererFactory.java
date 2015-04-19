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
package org.mascherl.render.mustache;

import org.mascherl.application.MascherlApplication;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.render.MascherlRendererFactory;

/**
 * Mustache implementation of {@link MascherlRendererFactory}.
 *
 * @author Jakob Korherr
 */
public class MustacheRendererFactory implements MascherlRendererFactory {

    private MascherlRenderer cachedRenderer;
    private MascherlApplication application;

    @Override
    public void init(MascherlApplication application) {
        this.application = application;
        if (!application.isDevelopmentMode()) {
            cachedRenderer = new MustacheRenderer(application.getServletContext());
        }
    }

    @Override
    public MascherlRenderer createMascherlRenderer() {
        if (application.isDevelopmentMode()) {
            return new MustacheRenderer(application.getServletContext());
        } else {
            return cachedRenderer;
        }
    }

}
