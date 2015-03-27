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
@Path("/page1/dialog/2")
@Template("/templates/dialog/dialog-page2.html")
public class Page1Dialog2 implements MascherlPage {

    @Override
    public String getTitle() {
        return "Page1 - Dialog - 2";
    }

    @Container("main")
    public Model main() {
        return new Model();
    }

    @Container("dialog")
    public Model dialog() {
        return new Model();
    }

    @Container("dialogContent")
    public Model dialogContent() {
        return new Model();
    }

}
