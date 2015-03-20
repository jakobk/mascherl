package org.macherl.test;

import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/page1/dialog")
public class Page1Dialog extends Page1 {

    @QueryParam("dialog-page")
    @DefaultValue("1")
    private String dialogPage;

    @Override
    public String getTitle() {
        return "Page1 - Dialog";
    }

    @Container("dialog")
    public Mascherl dialog() {
        return new Mascherl("/templates/dialog/test-dialog.html");
    }

    @Container("dialog-content")
    public Mascherl dialogContent() {
        return new Mascherl("/templates/dialog/test-dialog-content-" + dialogPage + ".html");
    }

}
