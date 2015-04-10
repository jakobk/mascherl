package org.mascherl.test;

import org.mascherl.page.FormResult;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.session.MascherlSession;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.time.LocalDateTime;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class OverviewPage {

    @GET
    @Path("/")
    public MascherlPage overview() {
        MascherlSession session = MascherlSession.getInstance();
        session.put("user", "Jakob Korherr");
        session.put("lastLogin", LocalDateTime.now());

        return Mascherl.page("/templates/overview.html")
                .pageTitle("Overview")
                .container("main")
                .container("links", (model) -> model.put("welcome", "Welcome to Mascherl!"))
                .container("form", (model) -> model
                        .put("message", "default message")
                        .put("overviewFormAction", UriBuilder.fromMethod(OverviewPage.class, "submit").build()));
    }

    @POST
    @Path("/")
    public FormResult submit(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        String message = "Hello " + overviewForm.getFirstname() + " " + overviewForm.getLastname();
        return Mascherl.stay().renderContainer("form").withPageDef(
                overview()
                        .container("form", (model) -> model.put("message", message)));
    }

    @POST
    @Path("/submit2")
    public FormResult submit2(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        return Mascherl.navigate("/page1").renderAll().withPageDef(page1());
    }

    @GET
    @Path("/page1")
    public MascherlPage page1() {
        MascherlSession session = MascherlSession.getInstance();
        System.out.println(session.getString("user"));
        System.out.println(session.get("lastLogin", LocalDateTime.class));

        return Mascherl.page("/templates/page1.html")
                .pageTitle("Page1")
                .container("main");
    }

    @GET
    @Path("/page1/dialog/1")
    public MascherlPage page1Dialog1() {
        return Mascherl.page("/templates/dialog/dialog-page1.html")
                .pageTitle("Page1 - Dialog")
                .container("main")
                .container("dialog")
                .container("dialogContent");
    }

    @GET
    @Path("/page1/dialog/2")
    public MascherlPage page1Dialog2() {
        return Mascherl.page("/templates/dialog/dialog-page2.html")
                .pageTitle("Page1 - Dialog - 2")
                .container("main")
                .container("dialog")
                .container("dialogContent");
    }

    @GET
    @Path("/page2")
    public MascherlPage page2() {
        return Mascherl.page("/templates/page2.html")
                .pageTitle("Page2")
                .container("main");
    }

    @GET
    @Path("/data/1")
    public MascherlPage data1() {
        return Mascherl.page("/templates/data-page1.html")
                .pageTitle("Data")
                .container("main")
                .container("dataContainer");
    }

    @GET
    @Path("/data/2")
    public MascherlPage data2() {
        return Mascherl.page("/templates/data-page2.html")
                .pageTitle("Data")
                .container("main")
                .container("dataContainer");
    }

}
