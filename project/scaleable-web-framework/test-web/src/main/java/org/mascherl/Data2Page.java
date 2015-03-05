package org.mascherl;

import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;
import org.mascherl.page.Page;
import org.mascherl.page.PageTitle;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Page("/data2")
public class Data2Page {  // request scoped

    @PageTitle
    public String getTitle() {
        return "Data2";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/data2.html");
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
