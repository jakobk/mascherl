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
