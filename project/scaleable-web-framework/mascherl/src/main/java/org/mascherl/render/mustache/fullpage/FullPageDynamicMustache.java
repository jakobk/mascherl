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

import com.github.mustachejava.Mustache;
import org.mascherl.render.mustache.wrapper.MustacheInterceptorWrapper;

/**
 * An interceptor-like wrapper of the full page Mustache, which sets the respective partial Mustache of the current
 * request in the {@link MainContainerPartialCode} before execution of any methods on the delegate, and removes the
 * partial again after execution
 *
 * Due to the dynamic inclusion of the respective partial Mustache, this Mustache must not be cached.
 *
 * @author Jakob Korherr
 */
public class FullPageDynamicMustache extends MustacheInterceptorWrapper {

    private final FullPageCachedMustache fullPage;
    private final Mustache partial;

    public FullPageDynamicMustache(FullPageCachedMustache fullPage, Mustache partial) {
        this.fullPage = fullPage;
        this.partial = partial;
    }

    @Override
    protected void before() {
        fullPage.getMainContainerPartialCode().getPartialThreadLocal().setMustache(partial);
    }

    @Override
    protected void after() {
        fullPage.getMainContainerPartialCode().getPartialThreadLocal().removeMustache();
    }

    @Override
    public Mustache getDelegate() {
        return fullPage;
    }

}
