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
import java.util.function.Supplier;

/**
 * A {@link MascherlAction} proxy, which creates the actual {@link MascherlAction} and delegates to it at first access
 * of any of the getters of {@link MascherlAction}.
 *
 * Necessary, if the creation of the actual MascherlAction requires a certain thread, which is not available right now,
 * e.g. in asynchronous request processing, if the creation of the MascherlAction needs to run on a container thread
 * with the container context objects in place (e.g. HttpServletRequest, HttpServletResponse, etc).
 *
 * @see org.mascherl.page.Mascherl#deferredAction(java.util.function.Supplier)
 *
 * @author Jakob Korherr
 */
public class DeferredMascherlAction extends MascherlAction {

    private final Supplier<MascherlAction> supplier;
    private MascherlAction mascherlAction;

    public DeferredMascherlAction(Supplier<MascherlAction> supplier) {
        this.supplier = supplier;
    }

    public MascherlAction getDelegate() {
        if (mascherlAction == null) {
            mascherlAction = supplier.get();
        }
        return mascherlAction;
    }

    @Override
    public String getPageGroup() {
        return getDelegate().getPageGroup();
    }

    @Override
    public URI getPageUrl() {
        return getDelegate().getPageUrl();
    }

    @Override
    public String getContainer() {
        return getDelegate().getContainer();
    }

    @Override
    public MascherlPage getMascherlPage() {
        return getDelegate().getMascherlPage();
    }

    @Override
    public MascherlAction withPageGroup(String pageGroup) {
        throw new UnsupportedOperationException("Not supported on deferred implementation");
    }
}
