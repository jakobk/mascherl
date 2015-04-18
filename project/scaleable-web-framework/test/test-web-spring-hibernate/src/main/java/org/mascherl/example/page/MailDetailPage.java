package org.mascherl.example.page;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailType;
import org.mascherl.example.domain.User;
import org.mascherl.example.service.MailService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mascherl.example.page.PageModelConverter.convertToPageModelForView;
import static org.mascherl.example.page.PageUtils.determineReturnToPage;
import static org.mascherl.example.page.PageUtils.parsePageParameter;

/**
 * Page class for the detail view of on mail.
 *
 * @author Jakob Korherr
 */
@Component
public class MailDetailPage {

    @Inject
    private User user;

    @Inject
    private MailInboxPage mailInboxPage;

    @Inject
    private MailService mailService;

    @GET
    @Path("/mail/{mailUuid}")
    public MascherlPage mailDetail(@PathParam("mailUuid") String mailUuid) {
        return Mascherl.page("/templates/mail/mailDetail.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("pageContent", (model) -> calculateContainerModel(mailUuid, user, model));
    }

    private void calculateContainerModel(String mailUuid, User user, Model model) {
        Mail mail = mailService.readMail(mailUuid, user);
        if (mail != null) {
            model.put("mail", convertToPageModelForView(mail));
        }
    }

    @POST
    @Path("/mail/{mailUuid}/delete")
    public MascherlAction deleteMail(
            @PathParam("mailUuid") String mailUuid,
            @FormParam("returnTo") @DefaultValue("/mail") String returnTo) {
        Mail mail = mailService.readMail(mailUuid, user);

        String whatWeDid;
        if (mail.getMailType() == MailType.TRASH) {
            mailService.permanentlyDeleteTrashMails(Collections.singletonList(mailUuid), user);
            whatWeDid = "permanently deleted";
        } else {
            mailService.moveToTrash(Collections.singletonList(mailUuid), user);
            whatWeDid = "moved to trash";
        }

        int page = parsePageParameter(returnTo);
        return Mascherl
                .navigate(returnTo)
                .renderContainer("content")
                .withPageDef(determineReturnToPage(mailInboxPage, returnTo, page)
                        .container("messages", (model) -> model.put("successMsg", "Mail " + whatWeDid + ".")))
                .withPageGroup("MailInboxPage");
    }

}
