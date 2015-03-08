package org.mascherl;

import org.mascherl.jaxrs.MascherlPage;
import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;
import org.mascherl.page.Page;

import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/page1")
public class Page1 implements MascherlPage {

    @Override
    public String getTitle() {
        return "Page1";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/page1.html");
    }


}
