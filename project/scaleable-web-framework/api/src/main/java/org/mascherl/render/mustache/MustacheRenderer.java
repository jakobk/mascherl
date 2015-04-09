package org.mascherl.render.mustache;

import com.github.mustachejava.Mustache;
import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.render.ContainerMeta;
import org.mascherl.render.MascherlRenderer;
import org.mascherl.render.TemplateMeta;

import javax.servlet.ServletContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.mascherl.MascherlConstants.MAIN_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_CONTAINER;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_PAGE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_TITLE;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_MASCHERL_URL;
import static org.mascherl.MascherlConstants.ResponseHeaders.X_POWERED_BY;

/**
 * MascherlRenderer implementation using Mustache as render engine.
 *
 * @author Jakob Korherr
 */
public class MustacheRenderer implements MascherlRenderer {

    private static final String SHA_256 = "SHA-256";

    private final MascherlMustacheFactory mustacheFactory;

    public MustacheRenderer(ServletContext servletContext) {
        mustacheFactory = new MascherlMustacheFactory(servletContext);
    }

    @Override
    public void renderFull(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                           OutputStream outputStream, MultivaluedMap<String, Object> httpHeaders) throws IOException {
        String pageId = calculatePageId(mascherlApplication, resourceInfo);
        addGeneralHttpHeaders(mascherlApplication, httpHeaders);
        render(mascherlApplication, page, pageId, outputStream, MAIN_CONTAINER, false);
    }

    @Override
    public void renderContainer(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                                OutputStream outputStream, MultivaluedMap<String, Object> httpHeaders,
                                String container, String clientUrl) throws IOException {
        String pageId = calculatePageId(mascherlApplication, resourceInfo);
        addGeneralHttpHeaders(mascherlApplication, httpHeaders);
        addHttpHeadersForPartialResponse(page, pageId, httpHeaders, container, clientUrl);
        render(mascherlApplication, page, pageId, outputStream, container, true);
    }

    private void render(MascherlApplication mascherlApplication, MascherlPage page, String pageId, OutputStream outputStream,
                        String container, boolean isPartial) throws IOException {
        Mustache mustache;
        if (isPartial) {
            mustache = getContainerMustache(container, page.getTemplate());
        } else {
            mustache = mustacheFactory.compileFullPage(page.getTemplate());
        }

        TemplateMeta templateMeta = mustacheFactory.getTemplateMeta(page.getTemplate());
        ContainerMeta containerMeta = templateMeta.getContainerMeta(container);
        Map<String, Model> containerModels = new HashMap<>();
        collectModelValues(page, containerMeta, containerModels);

        MustacheRendererScope scope = new MustacheRendererScope(mascherlApplication, page, pageId, containerModels);
        if (isPartial) {
            scope.setCurrentContainer(container);
        }

        mustache.execute(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), scope).flush();
    }

    private String calculatePageId(MascherlApplication mascherlApplication, ResourceInfo resourceInfo) {
        String pageId = resourceInfo.getResourceClass().getName();
        if (resourceInfo.getResourceMethod() != null) {
            pageId += "." + resourceInfo.getResourceMethod().getName();
        }

        if (!mascherlApplication.isDevelopmentMode()) {
            // SHA-256 plain resource page id in order to hide resource class + method
            pageId = sha256(pageId);
        }

        return pageId;
    }

    private String sha256(String value) {
        MessageDigest messageDigest = createMessageDigest();
        messageDigest.update(value.getBytes(StandardCharsets.UTF_8));
        byte[] digest = messageDigest.digest();
        byte[] base64Digest = Base64.getEncoder().encode(digest);
        return new String(base64Digest, StandardCharsets.UTF_8);
    }

    private static MessageDigest createMessageDigest() {
        try {
            return MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void collectModelValues(MascherlPage page, ContainerMeta containerMeta, Map<String, Model> containerModels) {
        Model model = new Model();
        page.populateContainerModel(containerMeta.getContainerName(), model);
        containerModels.put(containerMeta.getContainerName(), model);

        for (ContainerMeta childContainerMeta : containerMeta.getChildren()) {
            collectModelValues(page, childContainerMeta, containerModels);
        }
    }

    private void addGeneralHttpHeaders(MascherlApplication mascherlApplication, MultivaluedMap<String, Object> httpHeaders) {
        httpHeaders.putSingle(X_POWERED_BY, "Mascherl " + mascherlApplication.getMascherlVersion());
    }

    private void addHttpHeadersForPartialResponse(MascherlPage page,
                                                  String pageId,
                                                  MultivaluedMap<String, Object> httpHeaders,
                                                  String container, String clientUrl) {
        httpHeaders.putSingle(X_MASCHERL_TITLE, page.getPageTitle());
        httpHeaders.putSingle(X_MASCHERL_PAGE, pageId);
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
