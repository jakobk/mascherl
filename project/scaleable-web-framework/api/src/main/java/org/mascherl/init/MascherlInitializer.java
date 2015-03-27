package org.mascherl.init;

import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.Container;
import org.mascherl.page.ContainerRef;
import org.mascherl.page.FormSubmission;
import org.mascherl.page.Model;
import org.mascherl.page.Template;
import org.mascherl.render.mustache.MustacheRenderer;
import org.mascherl.version.ApplicationVersionProvider;
import org.mascherl.version.ConfigApplicationVersionProvider;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.net.URI;
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
        addPageClassMeta(builder);
        builder.build(servletContext);
    }

    private ApplicationVersionProvider getApplicationVersionProvider() {
        Iterator<ApplicationVersionProvider> providers = ServiceLoader.load(ApplicationVersionProvider.class).iterator();
        if (providers.hasNext()) {
            return providers.next();
        }
        return new ConfigApplicationVersionProvider();   // default impl
    }

    private void addPageClassMeta(MascherlContext.Builder builder) {
        // all public methods (inherited or directly declared in class)
        pageClasses.stream().forEach(pageClass -> {
            createPageClassMeta(builder, pageClass);
        });
    }

    private static void createPageClassMeta(MascherlContext.Builder contextBuilder, Class<?> pageClass) {
        PageClassMeta.Builder pageMetaBuilder = new PageClassMeta.Builder();
        pageMetaBuilder.setPageClass(pageClass);

        Template templateAnnotation = pageClass.getAnnotation(Template.class);
        if (templateAnnotation == null) {
            throw new IllegalArgumentException("MascherlPage class " + pageClass.getName() +
                    " not annotated with @" + Template.class.getSimpleName());
        }
        pageMetaBuilder.setPageTemplate(templateAnnotation.value());

        for (Method method : pageClass.getMethods()) {  // all public methods (inherited or directly declared in class)
            if (method.isAnnotationPresent(Container.class)) {
                addContainer(pageMetaBuilder, method);
            } else if (method.isAnnotationPresent(FormSubmission.class)) {
                addForm(pageMetaBuilder, method);
            }
        }
        contextBuilder.addPageClassMeta(pageClass, pageMetaBuilder.build());
    }

    private static void addContainer(PageClassMeta.Builder builder, Method method) {
        verifyReturnType(Container.class, method, Model.class);
        Container container = method.getAnnotation(Container.class);
        builder.addContainer(container.value(), method);
    }

    private static void addForm(PageClassMeta.Builder builder, Method method) {
        verifyReturnType(FormSubmission.class, method, ContainerRef.class, Class.class, URI.class, String.class, Void.TYPE);
        FormSubmission form = method.getAnnotation(FormSubmission.class);
        builder.addForm(form.value(), method);
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

}
