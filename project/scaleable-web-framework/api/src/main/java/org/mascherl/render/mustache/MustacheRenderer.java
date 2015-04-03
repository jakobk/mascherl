package org.mascherl.render.mustache;

import com.github.mustachejava.Mustache;
import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPageSpec;
import org.mascherl.page.Model;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.render.TemplateMeta;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_PAGE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_TITLE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_URL;

/**
 * MascherlRenderer implementation using Mustache as render engine.
 *
 * @author Jakob Korherr
 */
public class MustacheRenderer implements MascherlRenderer {

    private final MascherlMustacheFactory mustacheFactory;

    public MustacheRenderer(ServletContext servletContext) {
        mustacheFactory = new MascherlMustacheFactory(servletContext);
    }

    @Override
    public void renderFull(MascherlApplication mascherlApplication, MascherlPageSpec page, OutputStream outputStream,
                           MultivaluedMap<String, Object> httpHeaders) throws IOException {
        render(mascherlApplication, page, outputStream, MAIN_CONTAINER, false);
    }

    @Override
    public void renderContainer(MascherlApplication mascherlApplication, MascherlPageSpec page, OutputStream outputStream,
                                MultivaluedMap<String, Object> httpHeaders,
                                String container, String clientUrl) throws IOException {
        addHttpHeadersForPartialResponse(page, httpHeaders, container, clientUrl);
        render(mascherlApplication, page, outputStream, container, true);
    }

    private void render(MascherlApplication mascherlApplication, MascherlPageSpec page, OutputStream outputStream, String container, boolean isPartial) throws IOException {
        Mustache mustache;
        if (isPartial) {
            mustache = getContainerMustache(container, page.getTemplate());
        } else {
            mustache = mustacheFactory.compileFullPage(page.getTemplate());
        }

        TemplateMeta templateMeta = mustacheFactory.getTemplateMeta(page.getTemplate());
        ContainerMeta containerMeta = templateMeta.getContainerMeta(container);
        List<Model> models = new LinkedList<>();
        collectModelValues(page, containerMeta, models);

        MustacheRendererScope scope = new MustacheRendererScope(mascherlApplication, page, models);
        mustache.execute(new OutputStreamWriter(outputStream), scope).flush();
    }

    private void collectModelValues(MascherlPageSpec page, ContainerMeta containerMeta, List<Model> models) {
        Model model = new Model();
        page.populateContainerModel(containerMeta.getContainerName(), model);
        models.add(model);

        for (ContainerMeta childContainerMeta : containerMeta.getChildren()) {
            collectModelValues(page, childContainerMeta, models);
        }
    }

    private void addHttpHeadersForPartialResponse(MascherlPageSpec page, MultivaluedMap<String, Object> httpHeaders,
                                                  String container, String clientUrl) {
        httpHeaders.putSingle(X_MASCHERL_TITLE, page.getPageTitle());
        httpHeaders.putSingle(X_MASCHERL_PAGE, page.getClass().getName());
        httpHeaders.putSingle(X_MASCHERL_CONTAINER, container);
        if (clientUrl != null) {
            httpHeaders.putSingle(X_MASCHERL_URL, clientUrl);
        }
    }

    private Mustache getContainerMustache(String container, String pageTemplate) {
        // first: make sure that the whole page template is fully compiled
        // (if so we get a cache hit in the MustacheFactory, and it won't get compiled again)
        Mustache mustache = mustacheFactory.compile(pageTemplate);
        // then: check if we should only render a sub-container, and directly use the sub-container mustache
        if (isSubContainer(container)) {
            mustache = mustacheFactory.getMustacheForContainer(pageTemplate, container);
        }
        return mustache;
    }

    private boolean isSubContainer(String container) {
        return !Objects.equals(container, MAIN_CONTAINER);
    }

}
