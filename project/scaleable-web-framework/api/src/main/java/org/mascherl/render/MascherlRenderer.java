package org.mascherl.render;

import org.mascherl.application.MascherlApplication;
import org.mascherl.page.MascherlPage;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Definition of a renderer for Mascherl.
 *
 * @author Jakob Korherr
 */
public interface MascherlRenderer {

    public String FULL_PAGE_RESOURCE = "/index.html";

    public void renderFull(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                           OutputStream outputStream, MultivaluedMap<String, Object> httpHeaders) throws IOException;

    public void renderContainer(MascherlApplication mascherlApplication, MascherlPage page, ResourceInfo resourceInfo,
                                OutputStream outputStream, MultivaluedMap<String, Object> httpHeaders,
                                String container, String clientUrl) throws IOException;

}
