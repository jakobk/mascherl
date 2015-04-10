package org.mascherl.render.mustache;

import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;

import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.mascherl.MascherlConstants.RootScopeVariables;

/**
 * The actual scope used for executing Mustaches, wrapping the container scope and additional rendering functions.
 *
 * @author Jakob Korherr
 */
public class MustacheRendererScope extends HashMap<String, Object> {

    private static final Logger logger = Logger.getLogger(MustacheRendererScope.class.getName());

    private final MascherlApplication mascherlApplication;
    private final MascherlPage page;
    private final String pageGroup;
    private String currentContainer;
    private final Set<String> warnedValues;

    public MustacheRendererScope(MascherlApplication mascherlApplication, MascherlPage page, String pageGroup) {
        this.mascherlApplication = mascherlApplication;
        this.page = page;
        this.pageGroup = pageGroup;
        if (mascherlApplication.isDevelopmentMode()) {
            warnedValues = new HashSet<>();
        } else {
            warnedValues = null;
        }
    }

    @Override
    public Object get(Object keyObject) {
        final String key = (String) keyObject;

        if (currentContainer != null) {
            Map<String, Object> containerScope = page.getContainerModels().get(currentContainer).getScope();
            if (containerScope.containsKey(key)) {
                return containerScope.get(key);
            }
        }
        if (Objects.equals(key, RootScopeVariables.TITLE)) {
            return page.getPageTitle();
        }
        if (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION)) {
            return mascherlApplication.getApplicationVersion().getVersion();
        }
        if (Objects.equals(key, RootScopeVariables.PAGE_GROUP)) {
            return pageGroup;
        }
        if (Objects.equals(key, RootScopeVariables.URL_FUNCTION)) {
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
        boolean found = (currentContainer != null && page.getContainerModels().get(currentContainer).getScope().containsKey(key))
                || (Objects.equals(key, RootScopeVariables.TITLE))
                || (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION))
                || (Objects.equals(key, RootScopeVariables.PAGE_GROUP))
                || (Objects.equals(key, RootScopeVariables.URL_FUNCTION));

        if (!found) {
            maybeDisplayWarningMessage(key);
        }

        return found;
    }

    private void maybeDisplayWarningMessage(String key) {
        if (mascherlApplication.isDevelopmentMode() && !warnedValues.contains(key)) {
            Set<String> foundInContainerModels = page.getContainerModels().entrySet().stream()
                    .filter((containerModel) -> containerModel.getValue().getScope().containsKey(key))
                    .map(Entry::getKey)
                    .collect(Collectors.toSet());
            if (!foundInContainerModels.isEmpty()) {
                logger.warning("Container {{$" + currentContainer + "}} tried to resolve value {{" + key +
                        "}}, but this value is only available in the model of the following container(s): " +
                        foundInContainerModels.stream().collect(Collectors.joining(", ")));
                warnedValues.add(key);
            }
        }
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    public String getCurrentContainer() {
        return currentContainer;
    }

    public void setCurrentContainer(String currentContainer) {
        this.currentContainer = currentContainer;
    }
}
