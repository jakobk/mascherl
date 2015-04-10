package org.mascherl.example.page;

import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.session.MascherlSession;
import org.mascherl.example.domain.User;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Path("/mail")
@Component
public class MailOverviewPage {

    @GET
    public MascherlPage index() {
        MascherlSession session = MascherlSession.getInstance();
        User user = session.get("user", User.class);

        return Mascherl.page("/templates/mailOverview.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("content", (model) -> model.put("user", user))
                .container("userInfo", (model) -> model.put("user", user));
    }
}
