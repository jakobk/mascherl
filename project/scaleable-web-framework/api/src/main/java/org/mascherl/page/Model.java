package org.mascherl.page;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Model} specifies the data which is needed to render a specific container on a page.
 *
 * It contains all data which is needed to render the related container template (push style).
 *
 * @author Jakob Korherr
 */
public class Model {

    private final Map<String, Object> scope = new HashMap<>();

    public Model() {
    }

    public Model put(String key, Object value) {
        scope.put(key, value);
        return this;
    }

    public Map<String, Object> getScope() {
        return scope;
    }
}
