package org.mascherl.test.page;

import org.mascherl.page.FormResult;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.session.MascherlSession;
import org.mascherl.test.domain.User;
import org.mascherl.test.service.LoginService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.time.LocalDateTime;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Component
public class IndexPage {

    @Inject
    private LoginService loginService;

    @GET
    @Path("/")
    public MascherlPage overview() {
        MascherlSession session = MascherlSession.getInstance();

        return Mascherl.page("/templates/overview.html")
                .pageTitle("Overview")
                .container("main", (model) -> {
                    User user = session.get("user", User.class);
                    String msg;
                    if (user != null) {
                        msg = "Hello " + user.getFirstName() + " " + user.getLastName() + "!";
                    } else {
                        msg = "Not logged in.";
                    }
                    model.put("msg", msg);
                });
    }

    @POST
    @Path("/login")
    public FormResult login(@FormParam("loginAlias") String loginAlias, @FormParam("password") String password) {
        MascherlSession session = MascherlSession.getInstance();
        User user = loginService.login(loginAlias, password);
        if (user != null) {
            session.put("user", user);
        }

        return Mascherl.stay().renderAll().withPageDef(overview());
    }


}
