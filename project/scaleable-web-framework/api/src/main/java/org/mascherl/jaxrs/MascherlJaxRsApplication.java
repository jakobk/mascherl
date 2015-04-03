package org.mascherl.jaxrs;

import org.mascherl.context.MascherlContext;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application for Mascherl.
 *
 * @author Jakob Korherr
 */
public class MascherlJaxRsApplication extends Application {

    private final ServletContext servletContext;

    public MascherlJaxRsApplication(@Context ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ApplicationVersionRequestFilter.class);
        classes.add(MascherlMessageBodyWriter.class);
        classes.add(MascherlResponseFilter.class);
        classes.addAll(MascherlContext.getInstance(servletContext).getPageClasses());
        return classes;
    }

}
