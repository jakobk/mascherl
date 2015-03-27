package org.mascherl.test;

import org.mascherl.page.ContainerRef;
import org.mascherl.page.FormSubmission;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Container;
import org.mascherl.page.Model;
import org.mascherl.page.Template;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/")
@Template("/templates/overview.html")
public class OverviewPage implements MascherlPage {  // request scoped

    private String message;

    @Override
    public String getTitle() {
        return "Overview";
    }

    @Container("main")
    public Model main() {
        return new Model().put("welcome", "Welcome to Mascherl!");
    }

    @Container("form")
    public Model form() {
        return new Model().put("message", message);
    }

    @FormSubmission("overview-form")
    public ContainerRef submit(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        message = "Hello " + overviewForm.getFirstname() + " " + overviewForm.getLastname();
        // return UriBuilder.fromResource(Page1.class).build();
        return new ContainerRef("form");
    }

}
