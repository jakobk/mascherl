package org.mascherl.render.mustache;

import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;

import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.mascherl.MascherlConstants.RootScopeVariables;

/**
 * The actual scope used for executing Mustaches, wrapping the container scope and additional rendering functions.
 *
 * @author Jakob Korherr
 */
public class MustacheRendererScope extends HashMap<String, Object> {

    private final MascherlApplication mascherlApplication;
    private final MascherlPage page;
    private final List<Model> models;

    public MustacheRendererScope(MascherlApplication mascherlApplication, MascherlPage page, List<Model> models) {
        this.mascherlApplication = mascherlApplication;
        this.page = page;
        this.models = models;
    }

    @Override
    public Object get(Object keyObject) {
        final String key = (String) keyObject;

        // TODO do not search all models for the value, only the model of the respective container
        Optional<Model> matchedModel = models.stream().filter((model) -> model.getScope().containsKey(key)).findAny();
        if (matchedModel.isPresent()) {
            return matchedModel.get().getScope().get(key);
        }
        if (Objects.equals(key, RootScopeVariables.TITLE)) {
            return page.getPageTitle();
        }
        if (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION)) {
            return mascherlApplication.getApplicationVersion().getVersion();
        }
        if (Objects.equals(key, RootScopeVariables.PAGE_ID)) {
            return page.getClass().getName();
        }
        if (Objects.equals(key, RootScopeVariables.URL)) {
            return (Function<String, String>) (resourceRef) -> {
                int classMethodSeparatorIndex = resourceRef.lastIndexOf(".");
                String className = resourceRef.substring(0, classMethodSeparatorIndex);
                String methodName = resourceRef.substring(classMethodSeparatorIndex + 1);
                Class<?> resourceClass;
                try {
                    resourceClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return UriBuilder.fromMethod(resourceClass, methodName).build().toString();
            };
        }
        return null;
    }

    @Override
    public boolean containsKey(Object keyObject) {
        final String key = (String) keyObject;
        return (models.stream().anyMatch((model) -> model.getScope().containsKey(key)))
                || (Objects.equals(key, RootScopeVariables.TITLE))
                || (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION))
                || (Objects.equals(key, RootScopeVariables.PAGE_ID))
                || (Objects.equals(key, RootScopeVariables.URL));
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }
}
