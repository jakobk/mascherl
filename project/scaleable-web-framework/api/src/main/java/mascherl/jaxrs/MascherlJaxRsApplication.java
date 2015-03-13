package mascherl.jaxrs;

import mascherl.context.MascherlContext;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
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
        return MascherlContext.getInstance(servletContext).getPageClasses();
    }

}
