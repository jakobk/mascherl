package org.mascherl.jaxrs;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface MascherlPage {

    @GET
    @Produces("text/html")
    public default Response get(@Context HttpServletRequest request) throws IOException {
        Map<String, Method> containerIndex = new HashMap<>();
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Container.class)) {  // TODO verify return type
                Container container = method.getDeclaredAnnotation(Container.class);
                containerIndex.put(container.value(), method);
            }
        }

        String container = request.getParameter("m-container");
        final boolean partialRequest = (container != null);

        String page = request.getParameter("m-page");
        if (page != null && !Objects.equals(page, getClass().getName())) {
            container = "main";
        }

        String pageTitle = getTitle();

        String resourcePath;
        Mascherl mascherl;
        if (partialRequest) {
            Method method = containerIndex.get(container);
            Object[] params = new Object[method.getParameterCount()];
            if (params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    params[i] = request;
                }
            }
            try {
                mascherl = (Mascherl) method.invoke(this, params);
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
            Mustache mustache = mustacheFactory.compile(Files.newBufferedReader(path), path.toString());

            Response.ResponseBuilder response = Response.ok();
            if (partialRequest) {
                response.header("X-Mascherl-Title", pageTitle);
                response.header("X-Mascherl-Page", getClass().getName());
                response.header("X-Mascherl-Container", container);
            }

            final Mascherl finalMascherl = mascherl;
            return response.entity((StreamingOutput) outputStream -> {
                mustache.execute(new OutputStreamWriter(outputStream), new MascherlScope(pageTitle, this, finalMascherl, containerIndex, request, mustacheFactory)).flush();
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
        private final Map<String, Method> containerIndex;
        private final HttpServletRequest request;
        private final MustacheFactory mustacheFactory;

        MascherlScope(String pageTitle, Object pageInstance, Mascherl mascherl, Map<String, Method> containerIndex, HttpServletRequest request, MustacheFactory mustacheFactory) {
            this.pageTitle = pageTitle;
            this.pageInstance = pageInstance;
            this.mascherl = mascherl;
            this.containerIndex = containerIndex;
            this.request = request;
            this.mustacheFactory = mustacheFactory;
        }

        @Override
        public Object get(Object key) {
            if (mascherl != null && mascherl.getScope().containsKey(key)) {
                return mascherl.getScope().get(key);
            }
            if (Objects.equals(key, "title")) {
                return pageTitle;
            }

            Method method = containerIndex.get(key);
            if (method != null) {
                Mascherl mascherl;
                Object[] params = new Object[method.getParameterCount()];
                if (params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        params[i] = request;
                    }
                }
                try {
                    mascherl = (Mascherl) method.invoke(pageInstance, params);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                try {
                    String realPath = request.getServletContext().getRealPath(mascherl.getTemplate());
                    Path path = Paths.get(realPath);
                    Mustache mustache = mustacheFactory.compile(Files.newBufferedReader(path), path.toString());
                    StringWriter mustacheOutput = new StringWriter();

                    mustache.execute(mustacheOutput, new MascherlScope(pageTitle, pageInstance, mascherl, containerIndex, request, mustacheFactory)).flush();

                    mustacheOutput.close();
                    return "<div id=\"" + key + "\" m-page=\"" + pageInstance.getClass().getName() + "\">" + mustacheOutput.toString() + "</div>";
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }

        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException();
        }
    }


}
