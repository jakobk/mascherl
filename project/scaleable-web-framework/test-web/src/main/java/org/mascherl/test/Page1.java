package org.mascherl.test;

import org.mascherl.page.Container;
import org.mascherl.page.Model;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Template;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/page1")
@Template("/templates/page1.html")
public class Page1 implements MascherlPage {

    @Override
    public String getTitle() {
        return "Page1";
    }

    @Container("main")
    public Model main() {
        return new Model();
    }

}
