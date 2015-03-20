package org.macherl.test;

import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/data")
public class DataPage implements MascherlPage {  // request scoped

    @QueryParam("page")
    @DefaultValue("1")
    private String pageParam;

    @Override
    public String getTitle() {
        return "Data";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/data.html");
    }

    @Container("data-container")
    public Mascherl dataContainer() {
        return new Mascherl("/templates/data-page" + pageParam + ".html");
    }

}
