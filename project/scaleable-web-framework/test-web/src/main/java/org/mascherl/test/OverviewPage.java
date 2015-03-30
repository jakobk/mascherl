package org.mascherl.test;

import org.mascherl.page.ContainerRef;
import org.mascherl.page.FormSubmission;
import org.mascherl.page.MascherlPageSpec;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/")  // TODO needed for initializer for now, find something better
public class OverviewPage {

    private String message = "default message";

    @GET
    @Path("/")
    public MascherlPageSpec overview() {
        return new MascherlPageSpec("/templates/overview.html", "Overview")
                .container("main", (model) -> {})
                .container("links", (model) -> model.put("welcome", "Welcome to Mascherl!"))
                .container("form", (model) -> model.put("message", message));
    }

    @GET
    @Path("/page1")
    public MascherlPageSpec page1() {
        return new MascherlPageSpec("/templates/page1.html", "Page1")
                .container("main", (model) -> {});
    }

    @GET
    @Path("/page1/dialog/1")
    public MascherlPageSpec page1Dialog1() {
        return new MascherlPageSpec("/templates/dialog/dialog-page1.html", "Page1 - Dialog")
                .container("main", (model) -> {})
                .container("dialog", (model) -> {})
                .container("dialogContent", (model) -> {});
    }

    @Path("/page1/dialog/2")
    @GET
    public MascherlPageSpec page1Dialog2() {
        return new MascherlPageSpec("/templates/dialog/dialog-page2.html", "Page1 - Dialog - 2")
                .container("main", (model) -> {})
                .container("dialog", (model) -> {})
                .container("dialogContent", (model) -> {});
    }

    @GET
    @Path("/page2")
    public MascherlPageSpec page2() {
        return new MascherlPageSpec("/templates/page2.html", "Page2")
                .container("main", (model) -> {});
    }

    @GET
    @Path("/data/1")
    public MascherlPageSpec data1() {
        return new MascherlPageSpec("/templates/data-page1.html", "Data")
                .container("main", (model) -> {})
                .container("dataContainer", (model) -> {});
    }

    @GET
    @Path("/data/2")
    public MascherlPageSpec data2() {
        return new MascherlPageSpec("/templates/data-page2.html", "Data")
                .container("main", (model) -> {})
                .container("dataContainer", (model) -> {});
    }

    @FormSubmission("overview-form")
    public ContainerRef submit(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        message = "Hello " + overviewForm.getFirstname() + " " + overviewForm.getLastname();
        // return UriBuilder.fromResource(Page1.class).build();
        return new ContainerRef("form");
    }

}
