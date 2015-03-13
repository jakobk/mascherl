package org.mascherl;

import org.mascherl.page.MascherlPage;
import org.mascherl.page.Container;
import org.mascherl.page.FormSubmission;
import org.mascherl.page.Mascherl;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

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

    @FormSubmission("overview-form")
    public URI submit(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        return UriBuilder.fromResource(Page1.class).build();
    }

}
