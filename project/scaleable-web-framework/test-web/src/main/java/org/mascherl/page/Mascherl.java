package org.mascherl.page;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class Mascherl {

    private final String template;
    private final Map<String, Object> scope = new HashMap<>();

    public Mascherl(String template) {
        this.template = template;
    }

    public Mascherl set(String key, Object value) {
        scope.put(key, value);
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public Map<String, Object> getScope() {
        return Collections.unmodifiableMap(scope);
    }
}
