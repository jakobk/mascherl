package org.mascherl.page;

import org.mascherl.application.MascherlApplication;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.servlet.MascherlFilter;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.RequestParameters.M_CONTAINER;

/**
 * Utility class used for defining a fluent api for controller code.
 *
 * @author Jakob Korherr
 */
public class Mascherl {

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

    public static FormResult redirect(String pageUri) {
        return new FormResult(UriBuilder.fromUri(pageUri).build());
    }

    public static FormResultBuilder renderAll() {
        return renderContainer(MAIN_CONTAINER);
    }

    public static FormResultBuilder renderContainer(String container) {
        MascherlFilter.getRequest().setAttribute(M_CONTAINER, container);
        return new FormResultBuilder(container);
    }

    public static class FormResultBuilder {

        private final String container;

        public FormResultBuilder(String container) {
            this.container = container;
        }

        public FormResult ofPage(MascherlPage mascherlPage) {
            return new FormResult(container, mascherlPage);
        }

    }

}
