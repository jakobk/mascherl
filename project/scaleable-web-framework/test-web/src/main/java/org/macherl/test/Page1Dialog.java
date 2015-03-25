package org.macherl.test;

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
@Path("/page1/dialog")
public class Page1Dialog implements MascherlPage {

    @QueryParam("dialog-page")
    @DefaultValue("1")
    private String dialogPage;

    @Override
    public String getTitle() {
        return "Page1 - Dialog";
    }

    @Container("main")
    public Partial main() {
        return new Partial("/templates/page1.html");
    }

    @Container("dialog")
    public Partial dialog() {
        return new Partial("/templates/dialog/test-dialog.html");
    }

    @Container("dialog-content")
    public Partial dialogContent() {
        return new Partial("/templates/dialog/test-dialog-content-" + dialogPage + ".html");
    }

}
