package org.mascherl.jaxrs;

import org.mascherl.Data2Page;
import org.mascherl.DataPage;
import org.mascherl.OverviewPage;
import org.mascherl.Page1;
import org.mascherl.Page2;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@ApplicationPath("/")
public class MascherlJaxRsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(OverviewPage.class);
        classes.add(DataPage.class);
        classes.add(Data2Page.class);
        classes.add(Page1.class);
        classes.add(Page2.class);
        return classes;
    }
}
