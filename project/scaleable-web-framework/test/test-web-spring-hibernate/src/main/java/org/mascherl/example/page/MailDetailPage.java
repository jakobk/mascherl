package org.mascherl.example.page;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.User;
import org.mascherl.example.service.MailService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.session.MascherlSession;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static org.mascherl.example.page.PageModelConverter.convertToPageModelForView;

/**
 * Page class for the detail view of on mail.
 *
 * @author Jakob Korherr
 */
@Component
public class MailDetailPage {

    @Inject
    private MailService mailService;

    @GET
    @Path("/mail/{mailUuid}")
    public MascherlPage inbox(@PathParam("mailUuid") String mailUuid) {
        MascherlSession session = MascherlSession.getInstance();
        User user = session.get("user", User.class);

        return Mascherl.page("/templates/mail/mailDetail.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("pageContent", (model) -> calculateContainerModel(mailUuid, user, model));
    }

    private void calculateContainerModel(String mailUuid, User user, Model model) {
        Mail mail = mailService.readMail(mailUuid, user);
        model.put("mail", convertToPageModelForView(mail));
    }

}
