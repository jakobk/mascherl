package org.mascherl.test;

import org.mascherl.page.Container;
import org.mascherl.page.Partial;
import org.mascherl.page.MascherlPage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/page1")
public class Page1 implements MascherlPage {

    @QueryParam("dialog-page")
    @DefaultValue("1")
    private String dialogPage;

    @Override
    public String getTitle() {
        return "Page1";
    }

    @Container("main")
    public Partial main() {
        return new Partial("/templates/page1.html");
    }

}
