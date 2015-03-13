package org.mascherl;

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
@Path("/data2")
public class Data2Page implements MascherlPage {  // request scoped

    @QueryParam("page")
    @DefaultValue("1")
    private String pageParam;

    @Override
    public String getTitle() {
        return "Data2";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/data2.html");
    }

    @Container("data-container")
    public Mascherl dataContainer() {
        return new Mascherl("/templates/data-page" + pageParam + ".html");
    }

}
