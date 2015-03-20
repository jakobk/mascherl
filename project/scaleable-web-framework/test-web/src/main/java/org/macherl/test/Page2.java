package org.macherl.test;

import org.mascherl.page.MascherlPage;
import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;

import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/page2")
public class Page2 implements MascherlPage {

    @Override
    public String getTitle() {
        return "Page2";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/page2.html");
    }

}
