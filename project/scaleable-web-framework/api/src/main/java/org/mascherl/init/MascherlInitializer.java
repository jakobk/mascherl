package org.mascherl.init;

import org.mascherl.context.MascherlContext;
import org.mascherl.render.mustache.MustacheRenderer;
import org.mascherl.version.ApplicationVersionProvider;
import org.mascherl.version.ConfigApplicationVersionProvider;

import javax.servlet.ServletContext;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Initializes Mascherl by creating and initializing {@link MascherlContext}.
 *
 * @author Jakob Korherr
 */
public class MascherlInitializer {

    private final ServletContext servletContext;
    private final Set<Class<?>> pageClasses;

    public MascherlInitializer(ServletContext servletContext, Set<Class<?>> pageClasses) {
        this.servletContext = servletContext;
        this.pageClasses = pageClasses;
    }

    public void initialize() {
        MascherlContext.Builder builder = new MascherlContext.Builder();
        builder.setMascherlRenderer(new MustacheRenderer(servletContext));
        builder.setApplicationVersion(getApplicationVersionProvider().getApplicationVersion());
        builder.setPageClasses(pageClasses);
        builder.build(servletContext);
    }

    private ApplicationVersionProvider getApplicationVersionProvider() {
        Iterator<ApplicationVersionProvider> providers = ServiceLoader.load(ApplicationVersionProvider.class).iterator();
        if (providers.hasNext()) {
            return providers.next();
        }
        return new ConfigApplicationVersionProvider();   // default impl
    }

}
