package org.mascherl.page;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.cxf.jaxrs.model.BeanParamInfo;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.model.URITemplate;
import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.mascherl.context.MascherlContext;
import org.mascherl.context.PageClassMeta;
import org.mascherl.page.Mascherl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Interface for all Mascherl page controllers.
 *
 * @author Jakob Korherr
 */
public interface MascherlPage {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public default Response post(@Context HttpServletRequest request,
                                 @Context HttpServletResponse response,
                                 @FormParam("m-form") String form,
                                 @FormParam("m-container") String container,
                                 @FormParam("m-page") String page) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(getClass());

        Method formMethod = pageClassMeta.getFormMethod(form);
        Message message = JAXRSUtils.getCurrentMessage();
        ClassResourceInfo rootResource = JAXRSUtils.getRootResource(message);

        ServerProviderFactory serverProviderFactory = ServerProviderFactory.getInstance(message);
        for (Parameter parameter : formMethod.getParameters()) {
            if (parameter.isAnnotationPresent(BeanParam.class)) {
                if (serverProviderFactory.getBeanParamInfo(parameter.getType()) == null) {
                    serverProviderFactory.addBeanParamInfo(new BeanParamInfo(parameter.getType(), rootResource.getBus()));
                }
            }
        }

        @SuppressWarnings("unchecked")
        MultivaluedMap<String, String> matchedPathValues = (MultivaluedMap<String, String>) message.get(URITemplate.TEMPLATE_PARAMETERS);
        List<Object> parameterValues;
        try {
            parameterValues = JAXRSUtils.processParameters(new OperationResourceInfo(formMethod, rootResource), matchedPathValues, message);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

        Object formMethodResult;
        try {
            formMethodResult = formMethod.invoke(this, parameterValues.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new WebApplicationException(e);
        }

        UriBuilder uriBuilder;
        if (formMethodResult instanceof String) {
            uriBuilder = UriBuilder.fromUri((String) formMethodResult);
        } else if (formMethodResult instanceof URI) {
            uriBuilder = UriBuilder.fromUri((URI) formMethodResult);
        } else if (formMethodResult instanceof Class) {
            uriBuilder = UriBuilder.fromResource((Class) formMethodResult);
        } else if (formMethodResult == null) {
            uriBuilder = null;
        } else {
            return Response.serverError().build();
        }

        if (uriBuilder != null) {
            String clientUrl = uriBuilder.build().toString();
            request.setAttribute("m-client-url", clientUrl);

            uriBuilder.queryParam("m-container", "main");
            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {

                @Override
                public String getMethod() {
                    return "GET";  // emulate GET request
                }

            };
            try {
                request.getServletContext().getRequestDispatcher(uriBuilder.build().toString()).forward(requestWrapper, response);
            } catch (ServletException | IOException e) {
                throw new WebApplicationException(e);
            }
            return null;
        } else {
            // just render this page
            return get(request, container, page, false);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public default Response get(@Context HttpServletRequest request,
                                @QueryParam("m-container") String container,
                                @QueryParam("m-page") String page,
                                @QueryParam("m-server-redirect") Boolean serverRedirect) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(getClass());

        final boolean partialRequest = (container != null);

        if (page != null && !Objects.equals(page, getClass().getName())) {
            container = "main";
        }

        String pageTitle = getTitle();

        String resourcePath;
        Mascherl mascherl;
        if (partialRequest) {
            Method method = pageClassMeta.getContainerMethod(container);
            try {
                mascherl = (Mascherl) method.invoke(this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return Response.serverError().build();
            }
            resourcePath = mascherl.getTemplate();

        } else {
            mascherl = null;
            resourcePath = "/index.html";
        }
        String realPath = request.getServletContext().getRealPath(resourcePath);
        if (realPath == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Path path = Paths.get(realPath);
        if (Files.isReadable(path)) {
            MustacheFactory mustacheFactory = new DefaultMustacheFactory();
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(path);
            } catch (IOException e) {
                throw new WebApplicationException(e);
            }
            Mustache mustache = mustacheFactory.compile(reader, path.toString());

            Response.ResponseBuilder response = Response.ok();
            if (partialRequest) {
                response.header("X-Mascherl-Title", pageTitle);
                response.header("X-Mascherl-Page", getClass().getName());
                response.header("X-Mascherl-Container", container);
                String clientUrl = (String) request.getAttribute("m-client-url");
                if (clientUrl != null) {
                    response.header("X-Mascherl-Url", clientUrl);
                }
            }

            final Mascherl finalMascherl = mascherl;
            return response.entity((StreamingOutput) outputStream -> {
                mustache.execute(new OutputStreamWriter(outputStream), new MascherlScope(pageTitle, this, finalMascherl, pageClassMeta, request, mustacheFactory)).flush();
            }).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public default String getTitle() {
        return getClass().getSimpleName();
    }

    class MascherlScope extends HashMap<String, Object> {

        private final String pageTitle;
        private final Object pageInstance;
        private final Mascherl mascherl;
        private final PageClassMeta pageClassMeta;
        private final HttpServletRequest request;
        private final MustacheFactory mustacheFactory;

        MascherlScope(String pageTitle, Object pageInstance, Mascherl mascherl, PageClassMeta pageClassMeta, HttpServletRequest request, MustacheFactory mustacheFactory) {
            this.pageTitle = pageTitle;
            this.pageInstance = pageInstance;
            this.mascherl = mascherl;
            this.pageClassMeta = pageClassMeta;
            this.request = request;
            this.mustacheFactory = mustacheFactory;
        }

        @Override
        public Object get(Object keyObject) {
            final String key = (String) keyObject;

            if (mascherl != null && mascherl.getScope().containsKey(key)) {
                return mascherl.getScope().get(key);
            }
            if (Objects.equals(key, "title")) {
                return pageTitle;
            }
            if (key.startsWith("containers.")) {
                String containerName = key.substring("containers.".length());

                Method method = pageClassMeta.getContainerMethod(containerName);
                if (method != null) {
                    Mascherl mascherl;
                    try {
                        mascherl = (Mascherl) method.invoke(pageInstance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        String realPath = request.getServletContext().getRealPath(mascherl.getTemplate());
                        Path path = Paths.get(realPath);
                        Mustache mustache = mustacheFactory.compile(Files.newBufferedReader(path), path.toString());
                        StringWriter mustacheOutput = new StringWriter();

                        mustache.execute(mustacheOutput, new MascherlScope(pageTitle, pageInstance, mascherl, pageClassMeta, request, mustacheFactory)).flush();

                        mustacheOutput.close();
                        return "<div id=\"" + containerName + "\" m-page=\"" + pageInstance.getClass().getName() + "\">" + mustacheOutput.toString() + "</div>";
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return null;
        }

        @Override
        public boolean containsKey(Object keyObject) {
            final String key = (String) keyObject;
            return (mascherl != null && mascherl.getScope().containsKey(key))
                    || (key.startsWith("containers.") && pageClassMeta.containerExists(key.substring("containers.".length())))
                    || (Objects.equals(key, "title"));
        }

        @Override
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException();
        }
    }


}
