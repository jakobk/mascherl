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
    public Partial main() {
        return new Partial("/templates/data2.html");
    }

    @Container("data-container")
    public Partial dataContainer() {
        return new Partial("/templates/data-page" + pageParam + ".html");
    }

}
