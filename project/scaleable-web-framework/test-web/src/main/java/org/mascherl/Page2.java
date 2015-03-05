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
@Page("/page2")
public class Page2 {

    @PageTitle
    public String getTitle() {
        return "Page2";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/page2.html");
    }

}
