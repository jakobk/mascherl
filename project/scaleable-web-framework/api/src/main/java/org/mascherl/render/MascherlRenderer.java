package org.mascherl.render;

import org.mascherl.page.MascherlPage;

import javax.ws.rs.core.Response;

/**
 * Definition of a renderer for Mascherl.
 *
 * @author Jakob Korherr
 */
public interface MascherlRenderer {

    public String FULL_PAGE_RESOURCE = "/index.html";

    public Response renderFull(MascherlPage page);

    public Response renderContainer(MascherlPage page, String container, String clientUrl);

}
