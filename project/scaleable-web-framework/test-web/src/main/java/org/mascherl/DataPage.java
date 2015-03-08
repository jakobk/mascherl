package org.mascherl;

import org.mascherl.jaxrs.MascherlPage;
import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;
import org.mascherl.page.Page;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/data")
public class DataPage implements MascherlPage {  // request scoped

    @Override
    public String getTitle() {
        return "Data";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/data.html");
    }

    @Container("data-container")
    public Mascherl dataContainer(HttpServletRequest request) {
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }

        return new Mascherl("/templates/data-page" + page + ".html");
    }

}
