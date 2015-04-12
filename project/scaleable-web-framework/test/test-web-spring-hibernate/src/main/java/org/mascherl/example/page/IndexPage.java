package org.mascherl.example.page;

import org.mascherl.page.MascherlAction;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.session.MascherlSession;
import org.mascherl.example.domain.User;
import org.mascherl.example.service.LoginService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Component
public class IndexPage {

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
    public MascherlAction loginAction(@FormParam("email") String email, @FormParam("password") String password) {
        MascherlSession session = MascherlSession.getInstance();
        User user = loginService.login(email, password);
        if (user != null) {
            session.put("user", user);
            return Mascherl.navigate("/mail").renderAll().withPageDef(mailOverviewPage.inbox(1));
        } else {
            return Mascherl.navigate("/login").renderAll().withPageDef(
                    login().container("messages", (model) -> model.put("errorMsg", "Invalid email or password!")));
        }

    }

    @POST
    @Path("/logout")
    public MascherlAction logoutAction() {
        MascherlSession session = MascherlSession.getInstance();
        session.remove("user");
        return Mascherl.navigate("/login").renderAll().withPageDef(
                login().container("messages", (model) -> model.put("infoMsg", "Successfully signed out!")));

    }

}
