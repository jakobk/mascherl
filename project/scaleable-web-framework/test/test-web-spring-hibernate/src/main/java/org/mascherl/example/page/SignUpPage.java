package org.mascherl.example.page;

import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Page class for the sign-up dialog.
 *
 * @author Jakob Korherr
 */
@Component
public class SignUpPage {

    @GET
    @Path("/signup")
    public MascherlPage signUp() {
        return Mascherl.page("/templates/root/signupStep1.html")
                .pageTitle("SignUp - WebMail powered by Mascherl");
    }

}
