package org.macherl.test;

import mascherl.page.MascherlPage;
import mascherl.page.Container;
import mascherl.page.Mascherl;

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
