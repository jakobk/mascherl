package org.mascherl.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ServletContextListener initializing Mascherl upon servlet container startup.
 *
 * @author Jakob Korherr
 */
@WebListener
public class MascherlServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new MascherlInitializer(sce.getServletContext()).initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
