package org.mascherl.init;

import org.mascherl.page.MascherlPage;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.ws.rs.Path;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Initializes Mascherl using {@link ServletContainerInitializer} and {@link HandlesTypes}.
 *
 * Using this method of initialization has the advantage of not having to scan the classpath ourselves
 * for classes implementing {@link MascherlPage} (e.g. by using a tool like Scannotation) in order to build
 * the necessary page class metadata. The whole classpath scanning is left to the servlet container here.
 *
 * @author Jakob Korherr
 */
@HandlesTypes({Path.class}) // only works for TYPE annotations, thus must use @Path (Mascherl annotations are METHOD annotations)
public class MascherlContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        if (classes != null && !classes.isEmpty()) {
            Set<Class<?>> mascherlPageClasses = classes.stream().filter(this::isMascherlPage).collect(Collectors.toSet());
            if (!mascherlPageClasses.isEmpty()) {
                new MascherlInitializer(ctx, classes).initialize();
            }
        }
    }

    private boolean isMascherlPage(Class<?> pageClass) {
        return MascherlPage.class.isAssignableFrom(pageClass)
                && !Modifier.isAbstract(pageClass.getModifiers())
                && !pageClass.isInterface();
    }

}