package org.mascherl.test;

import org.mascherl.page.Container;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.page.Template;

import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/page2")
@Template("/templates/page2.html")
public class Page2 implements MascherlPage {

    @Override
    public String getTitle() {
        return "Page2";
    }

    @Container("main")
    public Model main() {
        return new Model();
    }

}
