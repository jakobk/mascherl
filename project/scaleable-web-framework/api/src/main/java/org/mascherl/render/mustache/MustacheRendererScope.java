package org.mascherl.render.mustache;

import org.mascherl.context.MascherlContext;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;

import java.util.HashMap;
import java.util.Objects;

import static org.mascherl.MascherlConstants.RootScopeVariables;

/**
 * The actual scope used for executing Mustaches, wrapping the container scope and additional rendering functions.
 *
 * @author Jakob Korherr
 */
public class MustacheRendererScope extends HashMap<String, Object> {

    private final MascherlContext mascherlContext;
    private final MascherlPage pageInstance;
    private final Model model;

    public MustacheRendererScope(MascherlContext mascherlContext, MascherlPage pageInstance, Model model) {
        this.mascherlContext = mascherlContext;
        this.pageInstance = pageInstance;
        this.model = model;
    }

    @Override
    public Object get(Object keyObject) {
        final String key = (String) keyObject;

        if (model != null && model.getScope().containsKey(key)) {
            return model.getScope().get(key);
        }
        if (Objects.equals(key, RootScopeVariables.TITLE)) {
            return pageInstance.getTitle();
        }
        if (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION)) {
            return mascherlContext.getApplicationVersion().getVersion();
        }
        if (Objects.equals(key, "pageId")) {
            return pageInstance.getClass().getName();
        }
        return null;
    }

    @Override
    public boolean containsKey(Object keyObject) {
        final String key = (String) keyObject;
        return (model != null && model.getScope().containsKey(key))
                || (Objects.equals(key, RootScopeVariables.TITLE))
                || (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION))
                || (Objects.equals(key, "pageId"));
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }
}
