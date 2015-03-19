package org.macherl.test;

import mascherl.page.MascherlPage;
import mascherl.page.Container;
import mascherl.page.Mascherl;

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
    public Mascherl main() {
        return new Mascherl("/templates/page1.html");
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
