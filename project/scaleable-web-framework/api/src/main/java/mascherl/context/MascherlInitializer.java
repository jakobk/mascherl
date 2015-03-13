package mascherl.context;

import mascherl.page.Container;
import mascherl.page.FormSubmission;
import mascherl.page.Mascherl;
import mascherl.page.MascherlPage;
import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Set;

/**
 * Initializes Mascherl by creating the MascherlContext and adding all necessary class metadata to it.
 *
 * Furthermore, adds additional metadata to CXF for correct processing.
 *
 * @author Jakob Korherr
 */
public class MascherlInitializer {

    private final ServletContext servletContext;

    public MascherlInitializer(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void initialize() {
        MascherlContext mascherlContext = MascherlContext.createInstance(servletContext);

        buildPageClassMeta(mascherlContext);
    }

    private void buildPageClassMeta(MascherlContext mascherlContext) {
        AnnotationDB annotationDB = buildAnnotationDb();
        Set<String> pageClasses = annotationDB.getAnnotationIndex().get(Container.class.getName());
        for (String pageClass : pageClasses) {
            Class<?> clazz = instantiateClass(pageClass);
            if (MascherlPage.class.isAssignableFrom(clazz)
                    && !Modifier.isAbstract(clazz.getModifiers())
                    && !clazz.isInterface()) {
                PageClassMeta pageMeta = new PageClassMeta(clazz);
                for (Method method : clazz.getMethods()) {  // all public methods (inherited or directly declared in class)
                    if (method.isAnnotationPresent(Container.class)) {
                        verifyReturnType(Container.class, method, Mascherl.class);
                        Container container = method.getAnnotation(Container.class);
                        pageMeta.addContainer(container.value(), method);
                    } else if (method.isAnnotationPresent(FormSubmission.class)) {
                        verifyReturnType(FormSubmission.class, method, Class.class, URI.class, String.class, Void.TYPE);
                        FormSubmission form = method.getAnnotation(FormSubmission.class);
                        pageMeta.addForm(form.value(), method);
                    }
                }
                mascherlContext.addPageClassMeta(clazz, pageMeta);
            }
        }
    }

    private AnnotationDB buildAnnotationDb() {
        AnnotationDB annotationDB = new AnnotationDB();
        try {
            annotationDB.scanArchives(WarUrlFinder.findWebInfClassesPath(servletContext));
            annotationDB.scanArchives(WarUrlFinder.findWebInfLibClasspaths(servletContext));
        } catch (IOException e) {
            throw new IllegalStateException("Cound not initialize Mascherl", e);
        }
        return annotationDB;
    }

    private static void verifyReturnType(Class<?> annotationType, Method method, Class<?>... expectedReturnTypes) {
        for (Class<?> expectedReturnType : expectedReturnTypes) {
            if (expectedReturnType.isAssignableFrom(method.getReturnType())) {
                return;  // found expected type
            }
        }
        throw new IllegalStateException(
                "Method " + method.toGenericString() +
                        " is annotated with " + annotationType.getName() +
                        " but does not return one of the expected types (" + toString(expectedReturnTypes) + ")");
    }

    private static String toString(Class<?>[] expectedReturnTypes) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> expectedReturnType : expectedReturnTypes) {
            sb.append(expectedReturnType.getName());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    private static Class<?> instantiateClass(String clazz) {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cound not initialize Mascherl", e);
        }
    }

}
