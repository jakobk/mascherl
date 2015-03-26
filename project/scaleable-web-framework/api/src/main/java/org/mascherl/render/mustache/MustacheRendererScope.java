package org.mascherl.render.mustache;

import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Partial;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

import static org.mascherl.MascherlConstants.RootScopeVariables;

/**
 * The actual scope used for executing Mustaches, wrapping the container scope and additional rendering functions.
 *
 * @author Jakob Korherr
 */
public class MustacheRendererScope extends HashMap<String, Object> {

    private final MascherlContext mascherlContext;
    private final MascherlPage pageInstance;
    private final Partial partial;
    private final PageClassMeta pageClassMeta;
    private final Function<String, String> containerRenderFn;

    public MustacheRendererScope(MascherlContext mascherlContext, MascherlPage pageInstance, Partial partial,
                                 PageClassMeta pageClassMeta, Function<String, String> containerRenderFn) {
        this.mascherlContext = mascherlContext;
        this.pageInstance = pageInstance;
        this.partial = partial;
        this.pageClassMeta = pageClassMeta;
        this.containerRenderFn = containerRenderFn;
    }

    @Override
    public Object get(Object keyObject) {
        final String key = (String) keyObject;

        if (partial != null && partial.getScope().containsKey(key)) {
            return partial.getScope().get(key);
        }
        if (Objects.equals(key, RootScopeVariables.TITLE)) {
            return pageInstance.getTitle();
        }
        if (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION)) {
            return mascherlContext.getApplicationVersion().getVersion();
        }
        if (key.startsWith("@")) {
            String containerName = key.substring(1);
            return renderSubContainer(containerName);
        }

        return null;
    }

    private String renderSubContainer(String containerName) {
        return containerRenderFn.apply(containerName);
    }

    @Override
    public boolean containsKey(Object keyObject) {
        final String key = (String) keyObject;
        return (partial != null && partial.getScope().containsKey(key))
                || (key.startsWith("@") && pageClassMeta.containerExists(key.substring(1)))
                || (Objects.equals(key, RootScopeVariables.TITLE))
                || (Objects.equals(key, RootScopeVariables.APPLICATION_VERSION));
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }
}
