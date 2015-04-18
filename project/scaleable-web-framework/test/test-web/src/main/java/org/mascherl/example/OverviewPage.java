/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.example;

import org.mascherl.page.MascherlAction;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.PageGroup;
import org.mascherl.session.MascherlSession;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.time.LocalDateTime;

/**
 * Page controller for the example pages.
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
    public MascherlAction submit(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        String message = "Hello " + overviewForm.getFirstname() + " " + overviewForm.getLastname();
        return Mascherl.stay().renderContainer("form").withPageDef(
                overview()
                        .container("form", (model) -> model.put("message", message)));
    }

    @POST
    @Path("/submit2")
    public MascherlAction submit2(@BeanParam OverviewForm overviewForm) {
        System.out.println(overviewForm.getFirstname() + " " + overviewForm.getLastname());
        return Mascherl.navigate("/page1").renderAll().withPageDef(page1()).withPageGroup("page1");
    }

    @GET
    @Path("/page1")
    @PageGroup("page1")
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
    @PageGroup("page1")
    public MascherlPage page1Dialog1() {
        return Mascherl.page("/templates/dialog/dialog-page1.html")
                .pageTitle("Page1 - Dialog")
                .container("main")
                .container("dialog")
                .container("dialogContent");
    }

    @GET
    @Path("/page1/dialog/2")
    @PageGroup("page1")
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
