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
@Path("/data/1")
@Template("/templates/data-page1.html")
public class DataPage1 implements MascherlPage {  // request scoped

    @Override
    public String getTitle() {
        return "Data";
    }

    @Container("main")
    public Model main() {
        return new Model();
    }

    @Container("dataContainer")
    public Model dataContainer() {
        return new Model();
    }

}
