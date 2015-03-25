package org.mascherl.page;

import java.util.HashMap;
import java.util.Map;

/**
 * A partial specifies the content of a container on the related page.
 *
 * It already contains the complete scope, which is needed to render the given template (push style).
 *
 * @author Jakob Korherr
 */
public class Partial {

    private final String template;
    private final Map<String, Object> scope = new HashMap<>();

    public Partial(String template) {
        this.template = template;
    }

    public Partial set(String key, Object value) {
        scope.put(key, value);
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public Map<String, Object> getScope() {
        return scope;
    }
}
