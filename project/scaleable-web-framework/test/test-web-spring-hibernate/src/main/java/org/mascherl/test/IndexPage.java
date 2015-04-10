package org.mascherl.test;

import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.session.MascherlSession;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.LocalDateTime;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class IndexPage {

    @GET
    @Path("/")
    public MascherlPage overview() {
        MascherlSession session = MascherlSession.getInstance();
        session.put("user", "Jakob Korherr");
        session.put("lastLogin", LocalDateTime.now());

        return Mascherl.page("/templates/overview.html")
                .pageTitle("Overview")
                .container("main", (model) -> model.put("msg", "Hello!!"));
    }

}
