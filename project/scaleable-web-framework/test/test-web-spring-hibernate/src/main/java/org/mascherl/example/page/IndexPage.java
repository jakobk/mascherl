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
package org.mascherl.example.page;

import org.mascherl.example.domain.User;
import org.mascherl.example.page.data.LoginBean;
import org.mascherl.example.service.LoginService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.mascherl.session.MascherlSession;
import org.mascherl.validation.ValidationResult;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Page controller for the root page of the application, and login and logout actions.
 *
 * @author Jakob Korherr
 */
@Component
public class IndexPage {

    @Inject
    private ValidationResult validationResult;

    @Inject
    private MascherlSession session;

    @Inject
    private LoginService loginService;

    @Inject
    private MailInboxPage mailOverviewPage;

    @GET
    @Path("/")
    public MascherlPage start() {
        return Mascherl.page("/templates/root/start.html")
                .pageTitle("WebMail powered by Mascherl");
    }

    @GET
    @Path("/login")
    public MascherlPage login() {
        return Mascherl.page("/templates/root/login.html")
                .pageTitle("Login - WebMail powered by Mascherl");
    }

    @POST
    @Path("/login")
    public MascherlAction loginAction(@Valid @BeanParam LoginBean loginBean) {
        User user;
        if (validationResult.isValid()) {
            user = loginService.login(loginBean.getEmail(), loginBean.getPassword());
        } else {
            user = null;
        }
        if (user != null) {
            session.put("user", user);
            return Mascherl
                    .navigate("/mail")
                    .renderAll()
                    .withPageDef(mailOverviewPage.inbox(1))
                    .withPageGroup("MailInboxPage");
        } else {
            return Mascherl
                    .navigate("/login")
                    .renderAll()
                    .withPageDef(login().container("messages", (model) -> model.put("errorMsg", "Invalid email or password!")));
        }

    }

    @POST
    @Path("/logout")
    public MascherlAction logoutAction() {
        MascherlSession session = MascherlSession.getInstance();
        session.remove("user");
        return Mascherl
                .navigate("/login")
                .renderAll()
                .withPageDef(login().container("messages", (model) -> model.put("infoMsg", "Successfully signed out!")));

    }

}
