package org.mascherl.servlet;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;
import org.mascherl.page.Page;
import org.mascherl.page.PageTitle;
import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This servlet handles all HTTP requests to Mascherl.
 *
 * @author Jakob Korherr
 */
public class MascherlServlet extends HttpServlet {

    private MustacheFactory mustacheFactory;
    private Map<String, Class<?>> pageIndex = new HashMap<>();
    private Map<String, Map<String, Method>> pageContainerIndex = new HashMap<>();
    private Map<String, Method> pageTitleIndex = new HashMap<>();

    @Override
    public void init() throws ServletException {
        mustacheFactory = new DefaultMustacheFactory();


        AnnotationDB annotationDB = new AnnotationDB();
        try {
            annotationDB.scanArchives(WarUrlFinder.findWebInfClassesPath(getServletContext()));
        } catch (IOException e) {
            throw new ServletException(e);
        }


        Set<String> pageClasses = annotationDB.getAnnotationIndex().get(Page.class.getName());
        for (String pageClass : pageClasses) {
            Class<?> clazz;
            try {
                clazz = Class.forName(pageClass);
            } catch (ClassNotFoundException e) {
                throw new ServletException(e);
            }

            Page page = clazz.getDeclaredAnnotation(Page.class);
            pageIndex.put(page.value(), clazz);
            Map<String, Method> containerConfig = new HashMap<>();
            pageContainerIndex.put(page.value(), containerConfig);

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Container.class)) {  // TODO verify return type
                    Container container = method.getDeclaredAnnotation(Container.class);
                    containerConfig.put(container.value(), method);
                } else if (method.isAnnotationPresent(PageTitle.class)) {  // TODO verify return type
                    pageTitleIndex.put(page.value(), method);
                }
            }
        }

    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String container = request.getParameter("m-container");
        final boolean partialRequest = (container != null);

        Class<?> pageClazz = pageIndex.get(request.getPathInfo());
        if (pageClazz == null) {
            response.sendError(404);  // TODO otherwise()
            return;
        }

        Object pageInstance;
        try {
            pageInstance = pageClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException(e);
        }

        String page = request.getParameter("m-page");
        if (page != null && !Objects.equals(page, pageInstance.getClass().getName())) {
            container = "main";
        }

        String pageTitle;
        try {
            pageTitle = (String) pageTitleIndex.get(request.getPathInfo()).invoke(pageInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ServletException(e);
        }

        String resourcePath;
        Mascherl mascherl;
        if (partialRequest) {
            Method method = pageContainerIndex.get(request.getPathInfo()).get(container);
            Object[] params = new Object[method.getParameterCount()];
            if (params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    params[i] = request;
                }
            }
            try {
                mascherl = (Mascherl) method.invoke(pageInstance, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ServletException(e);
            }
            resourcePath = mascherl.getTemplate();

        } else {
            mascherl = null;
            resourcePath = "/index.html";
        }
        URL resource = getServletContext().getResource(resourcePath);
        if (resource == null) {
            response.sendError(404);
            return;
        }
        Path path;
        try {
            path = Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            response.sendError(404);
            return;
        }

        if (Files.isReadable(path)) {
            response.setHeader("Content-type", "text/html; charset=utf-8");

            Mustache mustache = mustacheFactory.compile(Files.newBufferedReader(path), path.toString());

            if (partialRequest) {
                response.setHeader("X-Mascherl-Title", pageTitle);
                response.setHeader("X-Mascherl-Page", pageInstance.getClass().getName());
                response.setHeader("X-Mascherl-Container", container);
            }

            mustache.execute(response.getWriter(), new MascherlScope(pageTitle, pageInstance, mascherl, pageContainerIndex.get(request.getPathInfo()), request)).flush();
        } else {
            response.sendError(404);
        }
    }

    class MascherlScope extends HashMap<String, Object> {

        private final String pageTitle;
        private final Object pageInstance;
        private final Mascherl mascherl;
        private final Map<String, Method> containerIndex;
        private final HttpServletRequest request;

        MascherlScope(String pageTitle, Object pageInstance, Mascherl mascherl, Map<String, Method> containerIndex, HttpServletRequest request) {
            this.pageTitle = pageTitle;
            this.pageInstance = pageInstance;
            this.mascherl = mascherl;
            this.containerIndex = containerIndex;
            this.request = request;
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
                    URL resource = getServletContext().getResource(mascherl.getTemplate());
                    Path path = Paths.get(resource.toURI());
                    Mustache mustache = mustacheFactory.compile(Files.newBufferedReader(path), path.toString());
                    StringWriter mustacheOutput = new StringWriter();

                    mustache.execute(mustacheOutput, new MascherlScope(pageTitle, pageInstance, mascherl, containerIndex, request)).flush();

                    mustacheOutput.close();
                    return "<div id=\"" + key + "\" m-page=\"" + pageInstance.getClass().getName() + "\">" + mustacheOutput.toString() + "</div>";
                } catch (URISyntaxException | IOException e) {
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
