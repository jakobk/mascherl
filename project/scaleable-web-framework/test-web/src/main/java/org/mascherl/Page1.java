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
@Page("/page1")
public class Page1 {

    @PageTitle
    public String getTitle() {
        return "Page1";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/page1.html");
    }


}
