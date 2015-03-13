package mascherl.page;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import mascherl.context.MascherlContext;
import mascherl.context.PageClassMeta;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import static mascherl.MascherlConstants.MAIN_CONTAINER;
import static mascherl.MascherlConstants.M_CLIENT_URL;
import static mascherl.MascherlConstants.M_CONTAINER;
import static mascherl.MascherlConstants.M_FORM;
import static mascherl.MascherlConstants.M_PAGE;
import static mascherl.MascherlConstants.X_MASCHERL_CONTAINER;
import static mascherl.MascherlConstants.X_MASCHERL_PAGE;
import static mascherl.MascherlConstants.X_MASCHERL_TITLE;
import static mascherl.MascherlConstants.X_MASCHERL_URL;
import static mascherl.page.MascherlPageUtils.createUriBuilder;
import static mascherl.page.MascherlPageUtils.forwardAsGetRequest;
import static mascherl.page.MascherlPageUtils.invokeWithInjectedJaxRsParameters;

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
                                 @FormParam(M_FORM) String form,
                                 @FormParam(M_CONTAINER) String container,
                                 @FormParam(M_PAGE) String page) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(getClass());

        Method formMethod = pageClassMeta.getFormMethod(form);
        if (formMethod == null) {
            // no form method found, thus POST is not supported by this page
            throw new WebApplicationException(Response.status(Response.Status.METHOD_NOT_ALLOWED).allow("GET").build());
        }

        Object formMethodResult = invokeWithInjectedJaxRsParameters(this, formMethod);

        UriBuilder uriBuilder = createUriBuilder(formMethodResult);
        if (uriBuilder != null) {
            String clientUrl = uriBuilder.build().toString();
            request.setAttribute(M_CLIENT_URL, clientUrl);

            uriBuilder.queryParam(M_CONTAINER, MAIN_CONTAINER);
            return forwardAsGetRequest(request, response, uriBuilder);
        } else {
            // no forward necessary
            return get(request, container, page);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public default Response get(@Context HttpServletRequest request,
                                @QueryParam(M_CONTAINER) String container,
                                @QueryParam(M_PAGE) String page) {
        MascherlContext mascherlContext = MascherlContext.getInstance();
        PageClassMeta pageClassMeta = mascherlContext.getPageClassMeta(getClass());

        final boolean partialRequest = (container != null);

        if (page != null && !Objects.equals(page, getClass().getName())) {
            container = MAIN_CONTAINER;
        }

        String pageTitle = getTitle();

        String resourcePath;
        Mascherl mascherl;
        if (partialRequest) {
            Method containerMethod = pageClassMeta.getContainerMethod(container);
            mascherl = (Mascherl) invokeWithInjectedJaxRsParameters(this, containerMethod);
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
            BufferedReader reader;
            try {
                reader = Files.newBufferedReader(path);
            } catch (IOException e) {
                throw new WebApplicationException(e);
            }
            Mustache mustache = mustacheFactory.compile(reader, path.toString());

            Response.ResponseBuilder response = Response.ok();
            if (partialRequest) {
                response.header(X_MASCHERL_TITLE, pageTitle);
                response.header(X_MASCHERL_PAGE, getClass().getName());
                response.header(X_MASCHERL_CONTAINER, container);
                String clientUrl = (String) request.getAttribute(M_CLIENT_URL);
                if (clientUrl != null) {
                    response.header(X_MASCHERL_URL, clientUrl);
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
        private final MascherlPage pageInstance;
        private final Mascherl mascherl;
        private final PageClassMeta pageClassMeta;
        private final HttpServletRequest request;
        private final MustacheFactory mustacheFactory;

        MascherlScope(String pageTitle, MascherlPage pageInstance, Mascherl mascherl, PageClassMeta pageClassMeta, HttpServletRequest request, MustacheFactory mustacheFactory) {
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

                Method containerMethod = pageClassMeta.getContainerMethod(containerName);
                if (containerMethod != null) {
                    Mascherl mascherl = (Mascherl) invokeWithInjectedJaxRsParameters(pageInstance, containerMethod);

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
