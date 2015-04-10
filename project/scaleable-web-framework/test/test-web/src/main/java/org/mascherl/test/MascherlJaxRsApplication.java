package org.mascherl.test;

import org.mascherl.application.MascherlApplication;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application for Mascherl.
 *
 * @author Jakob Korherr
 */
public class MascherlJaxRsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.addAll(MascherlApplication.getJaxRsClasses());
        classes.add(OverviewPage.class);
        return classes;
    }

}
