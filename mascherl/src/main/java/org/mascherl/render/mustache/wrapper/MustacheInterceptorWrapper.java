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

import com.github.mustachejava.Code;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.util.Node;

import java.io.Writer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wrapper for {@link Mustache} that also allows to implement an interceptor for delegate calls.
 *
 * @author Jakob Korherr
 */
public abstract class MustacheInterceptorWrapper implements Mustache {

    public abstract Mustache getDelegate();

    protected void before() {}

    protected void after() {}

    @Override
    public void append(String text) {
        before();
        try {
            getDelegate().append(text);
        } finally {
            after();
        }
    }

    @Override
    public Object clone() {
        before();
        try {
            return getDelegate().clone();
        } finally {
            after();
        }
    }

    @Override
    public Writer execute(Writer writer, Object scope) {
        before();
        try {
            return getDelegate().execute(writer, scope);
        } finally {
            after();
        }
    }

    @Override
    public Writer execute(Writer writer, Object[] scopes) {
        before();
        try {
            return getDelegate().execute(writer, scopes);
        } finally {
            after();
        }
    }

    @Override
    public Code[] getCodes() {
        before();
        try {
            return getDelegate().getCodes();
        } finally {
            after();
        }
    }

    @Override
    public void identity(Writer writer) {
        before();
        try {
            getDelegate().identity(writer);
        } finally {
            after();
        }
    }

    @Override
    public void init() {
        before();
        try {
            getDelegate().init();
        } finally {
            after();
        }
    }

    @Override
    public void setCodes(Code[] codes) {
        before();
        try {
            getDelegate().setCodes(codes);
        } finally {
            after();
        }
    }

    @Override
    public Writer run(Writer writer, Object[] scopes) {
        before();
        try {
            return getDelegate().run(writer, scopes);
        } finally {
            after();
        }
    }

    @Override
    public Node invert(String text) {
        before();
        try {
            return getDelegate().invert(text);
        } finally {
            after();
        }
    }

    @Override
    public Object clone(Set<Code> seen) {
        before();
        try {
            return getDelegate().clone(seen);
        } finally {
            after();
        }
    }

    @Override
    public String getName() {
        before();
        try {
            return getDelegate().getName();
        } finally {
            after();
        }
    }

    @Override
    public Node invert(Node node, String text, AtomicInteger position) {
        before();
        try {
            return getDelegate().invert(node, text, position);
        } finally {
            after();
        }
    }
}
