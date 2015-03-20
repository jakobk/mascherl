package org.mascherl.context;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.servlet.ServletContext;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
        Message message = JAXRSUtils.getCurrentMessage();
        if (message == null) {
            throw new IllegalStateException("No current message");
        }
        ServletContext servletContext = (ServletContext) message.get(AbstractHTTPDestination.HTTP_CONTEXT);
        if (servletContext == null) {
            throw new IllegalStateException("ServletContext not set in current message");
        }
        return getInstance(servletContext);
    }

    public static MascherlContext getInstance(ServletContext servletContext) {
        MascherlContext mascherlContext = (MascherlContext) servletContext.getAttribute(MASCHERL_CONTEXT);
        if (mascherlContext == null) {
            throw new IllegalStateException("Mascherl is not initialized");
        }
        return mascherlContext;
    }

    static MascherlContext createInstance(ServletContext servletContext) {
        MascherlContext mascherlContext = new MascherlContext();
        servletContext.setAttribute(MASCHERL_CONTEXT, mascherlContext);
        return mascherlContext;
    }

    // MascherlContext is shared by all threads, thus must be thread-safe
    private final ConcurrentMap<Class<?>, PageClassMeta> pageClassMetaMap = new ConcurrentHashMap<>();

    private MascherlContext() {
        // instance creation controlled by getInstance()
    }

    public PageClassMeta getPageClassMeta(Class<?> pageClass) {
        return pageClassMetaMap.get(pageClass);
    }

    public void addPageClassMeta(Class<?> pageClass, PageClassMeta pageClassMeta) {
        pageClassMetaMap.put(pageClass, pageClassMeta);
    }

    public Set<Class<?>> getPageClasses() {
        return pageClassMetaMap.keySet();
    }

}
