package org.mascherl.jaxrs;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.servlet.ServletContext;

/**
 * Utils related to JAX-RS.
 *
 * @author Jakob Korherr
 */
public class JaxRs {

    public static ServletContext getServletContextOfCurrentRequest() {
        Message message = JAXRSUtils.getCurrentMessage();
        if (message == null) {
            throw new IllegalStateException("No current message");
        }
        ServletContext servletContext = (ServletContext) message.get(AbstractHTTPDestination.HTTP_CONTEXT);
        if (servletContext == null) {
            throw new IllegalStateException("ServletContext not set in current message");
        }
        return servletContext;
    }

}
