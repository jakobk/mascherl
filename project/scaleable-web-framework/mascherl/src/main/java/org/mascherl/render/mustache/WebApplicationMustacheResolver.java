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

import com.github.mustachejava.MustacheResolver;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of {@link MustacheResolver} for resolving mustache templates in the web application archive.
 *
 * @author Jakob Korherr
 */
public class WebApplicationMustacheResolver implements MustacheResolver {

    private final ServletContext servletContext;

    public WebApplicationMustacheResolver(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Reader getReader(String resourceName) {
        return openPageTemplate(resourceName);
    }

    private BufferedReader openPageTemplate(String pageTemplate) {
        String realPath = servletContext.getRealPath(pageTemplate);
        if (realPath != null) {
            Path path = Paths.get(realPath);
            if (Files.isReadable(path)) {
                try {
                    return Files.newBufferedReader(path);
                } catch (IOException e) {
                    throw new WebApplicationException(e);
                }
            }
        }
        return null;
    }

}
