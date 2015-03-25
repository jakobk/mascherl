package org.mascherl.context;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.mascherl.jaxrs.JaxRs.getServletContextOfCurrentRequest;

/**
 * The singleton context of Mascherl (one instance for the whole web application).
 *
 * Stores metadata needed for the processing of requests.
 *
 * @author Jakob Korherr
 */
public class MascherlContext {

    private static final String MASCHERL_CONTEXT = "MASCHERL_CONTEXT";

    public static MascherlContext getInstance() {
        return getInstance(getServletContextOfCurrentRequest());
    }

    public static MascherlContext getInstance(ServletContext servletContext) {
        MascherlContext mascherlContext = (MascherlContext) servletContext.getAttribute(MASCHERL_CONTEXT);
        if (mascherlContext == null) {
            throw new IllegalStateException("Mascherl is not initialized");
        }
        return mascherlContext;
    }

    /**
     * Builder for MascherlContext.
     */
    public static final class Builder {

        private final Map<Class<?>, PageClassMeta> pageClassMetaMap = new HashMap<>();

        public void addPageClassMeta(Class<?> pageClass, PageClassMeta pageClassMeta) {
            pageClassMetaMap.put(pageClass, pageClassMeta);
        }

        public MascherlContext build(ServletContext servletContext) {
            MascherlContext mascherlContext = new MascherlContext(this);
            servletContext.setAttribute(MASCHERL_CONTEXT, mascherlContext);
            return mascherlContext;
        }

    }

    // MascherlContext is shared by all threads, thus must be thread-safe
    private final ConcurrentMap<Class<?>, PageClassMeta> pageClassMetaMap;

    private MascherlContext(Builder builder) {
        pageClassMetaMap = new ConcurrentHashMap<>(builder.pageClassMetaMap);
    }

    public PageClassMeta getPageClassMeta(Class<?> pageClass) {
        return pageClassMetaMap.get(pageClass);
    }

    public Set<Class<?>> getPageClasses() {
        return pageClassMetaMap.keySet();
    }

}
