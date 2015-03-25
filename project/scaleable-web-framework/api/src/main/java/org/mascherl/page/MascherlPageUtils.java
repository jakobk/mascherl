package org.mascherl.page;

import org.apache.cxf.jaxrs.model.BeanParamInfo;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.model.URITemplate;
import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BeanParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility methods used in {@link MascherlPage}.
 *
 * @author Jakob Korherr
 */
public class MascherlPageUtils {

    public static Response forwardAsGetRequest(HttpServletRequest request, HttpServletResponse response, UriBuilder uriBuilder) {
        try {
            request.getServletContext().getRequestDispatcher(uriBuilder.build().toString())
                    .forward(new DispatcherForwardServletRequest(request), response);
        } catch (ServletException | IOException e) {
            throw new WebApplicationException(e);
        }
        return null;  // this value won't be processed anymore, but we have to return something
    }

    public static UriBuilder createUriBuilder(Object formMethodResult) {
        UriBuilder uriBuilder;
        if (formMethodResult == null || formMethodResult instanceof ContainerRef) {
            return null;
        } else if (formMethodResult instanceof String) {
            uriBuilder = UriBuilder.fromUri((String) formMethodResult);
        } else if (formMethodResult instanceof URI) {
            uriBuilder = UriBuilder.fromUri((URI) formMethodResult);
        } else if (formMethodResult instanceof Class) {
            uriBuilder = UriBuilder.fromResource((Class) formMethodResult);
        } else {
            throw new WebApplicationException("Illegal return value of method annotated with " +
                    FormSubmission.class.getName() + ": " + formMethodResult);
        }
        return uriBuilder;
    }

    public static Object invokeWithInjectedJaxRsParameters(MascherlPage instance, Method method) {
        Message message = JAXRSUtils.getCurrentMessage();
        ClassResourceInfo rootResourceInfo = JAXRSUtils.getRootResource(message);
        MultivaluedMap<String, String> matchedPathValues = getCxfMatchedPathValues(message);

        ensureBeanParamInfoIsAvailable(method, message, rootResourceInfo);

        List<Object> parameterValues;
        try {
            // TODO cache OperationResourceInfo
            parameterValues = JAXRSUtils.processParameters(new OperationResourceInfo(method, rootResourceInfo), matchedPathValues, message);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

        Object formMethodResult;
        try {
            formMethodResult = method.invoke(instance, parameterValues.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new WebApplicationException(e);
        }
        return formMethodResult;
    }

    /**
     * CXF does not recognize Mascherl methods as JAX-RS relevant methods, thus it does not parse the relevant BeanParam
     * metadata for these methods, and hence we have to add this metadata here ourselves.
     *
     * @param method
     * @param message
     * @param rootResourceInfo
     */
    private static void ensureBeanParamInfoIsAvailable(Method method, Message message, ClassResourceInfo rootResourceInfo) {
        ServerProviderFactory serverProviderFactory = ServerProviderFactory.getInstance(message);
        for (Class<?> beanParamType : getAllBeanParamTypes(method)) {
            if (serverProviderFactory.getBeanParamInfo(beanParamType) == null) {
                serverProviderFactory.addBeanParamInfo(new BeanParamInfo(beanParamType, rootResourceInfo.getBus()));
            }
        }
    }

    private static Set<Class<?>> getAllBeanParamTypes(Method method) {
        Set<Class<?>> beanParamTypes = new HashSet<>();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(BeanParam.class)) {
                beanParamTypes.add(parameter.getType());
            }
        }
        return beanParamTypes;
    }

    @SuppressWarnings("unchecked")
    private static MultivaluedMap<String, String> getCxfMatchedPathValues(Message message) {
        return (MultivaluedMap<String, String>) message.get(URITemplate.TEMPLATE_PARAMETERS);
    }

    /**
     * Servlet request wrapper for a call to requestDispatcher.forward(), emulating a GET request.
     *
     * @author Jakob Korherr
     */
    private static class DispatcherForwardServletRequest extends HttpServletRequestWrapper {

        public DispatcherForwardServletRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getMethod() {
            return "GET";  // emulate GET request
        }

    }

}
