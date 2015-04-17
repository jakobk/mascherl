package org.mascherl.page;

import org.mascherl.application.MascherlApplication;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.servlet.MascherlFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;

/**
 * Class used for defining the fluent api for controller code.
 *
 * @author Jakob Korherr
 */
public class Mascherl {

    public static void async(AsyncResponse asyncResponse, HttpServletRequest request, HttpServletResponse response) {
        asyncResponse.register((CompletionCallback) throwable -> cleanupAsync());
        MascherlFilter.setRequest(request);
        MascherlFilter.setResponse(response);
    }

    public static void cleanupAsync() {
        MascherlFilter.setRequest(null);
        MascherlFilter.setResponse(null);
    }

    public static MascherlPage page(String template) {
        MascherlApplication application = getMascherlApplication();
        MascherlRenderer mascherlRenderer = application.getMascherlRendererFactory().createMascherlRenderer();
        String requestContainer = (String) MascherlFilter.getRequest().getAttribute(M_CONTAINER);
        if (requestContainer == null) {
            requestContainer = MAIN_CONTAINER;
        }
        ContainerMeta containerMeta = mascherlRenderer.getContainerMeta(template, requestContainer);
        return new MascherlPage(template, containerMeta);
    }

    private static MascherlApplication getMascherlApplication() {
        return MascherlApplication.getInstance(MascherlFilter.getRequest().getServletContext());
    }

    public static NavigationHolder stay() {
        return new NavigationHolder();
    }

    public static NavigationHolder navigate(String pageUri) {
        return navigate(UriBuilder.fromUri(pageUri).build());
    }

    public static NavigationHolder navigate(URI pageUri) {
        return new NavigationHolder(pageUri);
    }

    public static class NavigationHolder {

        private final URI pageUrl;

        public NavigationHolder() {
            this(null);
        }

        public NavigationHolder(URI pageUrl) {
            this.pageUrl = pageUrl;
        }

        public ContainerHolder renderAll() {
            return renderContainer(MAIN_CONTAINER);
        }

        public ContainerHolder renderContainer(String container) {
            MascherlFilter.getRequest().setAttribute(M_CONTAINER, container);
            return new ContainerHolder(pageUrl, container);
        }

        public MascherlAction redirect() {
            return new MascherlAction(pageUrl);
        }

    }

    public static class ContainerHolder {

        private final URI pageUrl;
        private final String container;

        public ContainerHolder(URI pageUrl, String container) {
            this.pageUrl = pageUrl;
            this.container = container;
        }

        public MascherlAction withPageDef(MascherlPage mascherlPage) {
            return new MascherlAction(container, pageUrl, mascherlPage);
        }

    }

}
