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
package org.mascherl.render.mustache.fullpage;

import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.PartialCode;
import org.mascherl.MascherlConstants;
import org.mascherl.render.mustache.MascherlMustacheFactory;
import org.mascherl.render.mustache.wrapper.ThreadLocalMustacheDelegate;

/**
 * Special {@link PartialCode}, which includes the current pageTemplate instead of a static resource.
 *
 * @author Jakob Korherr
 */
public class MainContainerPartialCode extends PartialCode {

    private final ThreadLocalMustacheDelegate partialThreadLocal = new ThreadLocalMustacheDelegate();

    public MainContainerPartialCode(TemplateContext tc, MascherlMustacheFactory cf) {
        super(tc, cf, MascherlConstants.MAIN_CONTAINER);
    }

    @Override
    public synchronized void init() {
        filterText();
        partial = partialThreadLocal;
    }

    public ThreadLocalMustacheDelegate getPartialThreadLocal() {
        return partialThreadLocal;
    }

}
