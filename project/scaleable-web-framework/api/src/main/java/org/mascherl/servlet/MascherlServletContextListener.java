package org.mascherl.servlet;

import org.mascherl.application.MascherlInitializer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Initializes Mascherl upon servlet context initialization.
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
