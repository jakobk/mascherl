package org.mascherl.test;

import org.mascherl.page.FormResult;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPageSpec;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/")  // TODO needed for initializer for now, find something better
public class OverviewPage {

    @GET
    @Path("/")
    public MascherlPageSpec overview() {
        return Mascherl.page()
                .template("/templates/overview.html")
                .pageTitle("Overview")
                .container("main")
                .container("links", (model) -> model.put("welcome", "Welcome to Mascherl!"))
                .container("form", (model) -> model.put("message", "default message"));
    }

    @POST
    @Path("/")
    public FormResult submit(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        String message = "Hello " + overviewForm.getFirstname() + " " + overviewForm.getLastname();
        return Mascherl.renderContainer(
                "form",
                overview()
                        .container("form", (model) -> model.put("message", message)));
//        return Mascherl.renderPage("/page1?id=1", page1());
//        return Mascherl.redirect("/page1");
    }

    @POST
    @Path("/submit2")
    public FormResult submit2(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        String message = "Hello " + overviewForm.getFirstname() + " " + overviewForm.getLastname();
        return Mascherl.renderContainer(
                "form",
                overview()
                        .container("form", (model) -> model.put("message", message)));
//        return Mascherl.renderPage("/page1?id=1", page1());
//        return Mascherl.redirect("/page1");
    }

    @GET
    @Path("/page1")
    public MascherlPageSpec page1() {
        return Mascherl.page()
                .template("/templates/page1.html")
                .pageTitle("Page1")
                .container("main");
    }

    @GET
    @Path("/page1/dialog/1")
    public MascherlPageSpec page1Dialog1() {
        return Mascherl.page()
                .template("/templates/dialog/dialog-page1.html")
                .pageTitle("Page1 - Dialog")
                .container("main")
                .container("dialog")
                .container("dialogContent");
    }

    @Path("/page1/dialog/2")
    @GET
    public MascherlPageSpec page1Dialog2() {
        return Mascherl.page()
                .template("/templates/dialog/dialog-page2.html")
                .pageTitle("Page1 - Dialog - 2")
                .container("main")
                .container("dialog")
                .container("dialogContent");
    }

    @GET
    @Path("/page2")
    public MascherlPageSpec page2() {
        return Mascherl.page()
                .template("/templates/page2.html")
                .pageTitle("Page2")
                .container("main");
    }

    @GET
    @Path("/data/1")
    public MascherlPageSpec data1() {
        return Mascherl.page()
                .template("/templates/data-page1.html")
                .pageTitle("Data")
                .container("main")
                .container("dataContainer");
    }

    @GET
    @Path("/data/2")
    public MascherlPageSpec data2() {
        return Mascherl.page()
                .template("/templates/data-page2.html")
                .pageTitle("Data")
                .container("main")
                .container("dataContainer");
    }

}
