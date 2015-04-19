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
package org.mascherl.render.mustache.wrapper;

import com.github.mustachejava.Mustache;

/**
 * A wrapper for {@link Mustache} that delegates to a Mustache from a ThreadLocal
 * (i.e. it delegates to a different Mustache instance for every thread).
 *
 * @author Jakob Korherr
 */
public class ThreadLocalMustacheDelegate extends MustacheInterceptorWrapper {

    private final ThreadLocal<Mustache> mustacheThreadLocal = new ThreadLocal<>();

    @Override
    public void init() {
        // do nothing, the delegates must be initialised separately before the are set here
    }

    @Override
    public Mustache getDelegate() {
        return mustacheThreadLocal.get();
    }

    public void setMustache(Mustache mustache) {
        mustacheThreadLocal.set(mustache);
    }

    public void removeMustache() {
        mustacheThreadLocal.remove();
    }

}
