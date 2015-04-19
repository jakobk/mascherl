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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.ExtendNameCode;

import java.io.Writer;

/**
 * Mascherl specific implementation of {@link ExtendNameCode} (i.e. {{$container}}{{/container}}).
 *
 * Adjusts the currently valid scope at execution time to the respective container scope.
 *
 * @author Jakob Korherr
 */
public class MascherlExtendNameCode extends ExtendNameCode {

    public MascherlExtendNameCode(TemplateContext templateContext, DefaultMustacheFactory df, Mustache mustache, String variable) {
        super(templateContext, df, mustache, variable);
    }

    @Override
    public Writer execute(Writer writer, Object[] scopes) {
        MustacheRendererScope mustacheRendererScope = findMustacheRendererScope(scopes);
        if (mustacheRendererScope == null) {
            throw new IllegalStateException("MustacheRendererScope not found in current scopes");
        }

        String previousContainer = mustacheRendererScope.getCurrentContainer();
        try {
            mustacheRendererScope.setCurrentContainer(getName());
            return super.execute(writer, new Object[] { mustacheRendererScope });
        } finally {
            mustacheRendererScope.setCurrentContainer(previousContainer);
        }
    }

    private static MustacheRendererScope findMustacheRendererScope(Object[] scopes) {
        for (Object scope : scopes) {
            if (scope instanceof MustacheRendererScope) {
                return (MustacheRendererScope) scope;
            }
        }
        return null;
    }

}
