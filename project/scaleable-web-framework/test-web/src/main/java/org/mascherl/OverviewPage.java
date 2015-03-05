package org.mascherl;

import org.mascherl.page.Container;
import org.mascherl.page.Mascherl;
import org.mascherl.page.Page;
import org.mascherl.page.PageTitle;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Page("/")
public class OverviewPage {  // request scoped

    @PageTitle
    public String getTitle() {
        return "Overview";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/overview.html")
                .set("welcome", "Welcome to Mascherl!");
    }

}
