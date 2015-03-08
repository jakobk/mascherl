package org.mascherl;

import org.mascherl.jaxrs.MascherlPage;
import org.mascherl.page.Container;
import org.mascherl.page.Form;
import org.mascherl.page.Mascherl;

import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/")
public class OverviewPage implements MascherlPage {  // request scoped

    @Override
    public String getTitle() {
        return "Overview";
    }

    @Container("main")
    public Mascherl main() {
        return new Mascherl("/templates/overview.html")
                .set("welcome", "Welcome to Mascherl!");
    }

    @Form("overview-form")
    public void submit(Object formData) {

    }

}
