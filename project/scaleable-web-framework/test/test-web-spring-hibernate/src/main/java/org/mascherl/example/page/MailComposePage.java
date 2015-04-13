package org.mascherl.example.page;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.User;
import org.mascherl.example.page.data.ComposeMailBean;
import org.mascherl.example.service.ComposeMailService;
import org.mascherl.example.service.SendMailService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mascherl.example.page.PageModelConverter.convertToPageModelForEdit;

/**
 * Page class for the mail composing page.
 *
 * @author Jakob Korherr
 */
@Component
public class MailComposePage {

    @Inject
    private User user;

    @Inject
    private MailInboxPage mailInboxPage;

    @Inject
    private ComposeMailService composeMailService;

    @Inject
    private SendMailService sendMailService;

    @GET
    @Path("/mail/compose/{mailUuid}")
    public MascherlPage compose(@PathParam("mailUuid") String mailUuid) {
        return Mascherl.page("/templates/mail/mailCompose.html")
                .pageTitle("Compose - WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("pageContent", (model) -> model.put("mail", convertToPageModelForEdit(composeMailService.openDraft(mailUuid, user))));
    }

    @POST
    @Path("/mail/compose")
    public MascherlAction composeNew() {
        String mailUuid = composeMailService.composeNewMail(user);
        return Mascherl
                .navigate(UriBuilder.fromMethod(getClass(), "compose").build(mailUuid))
                .renderContainer("content")
                .withPageDef(compose(mailUuid));
    }

    @POST
    @Path("/mail/send/{mailUuid}")
    public MascherlAction send(@PathParam("mailUuid") String mailUuid, @BeanParam ComposeMailBean composeMailBean) {
        Mail draft = composeMailService.openDraft(mailUuid, user);
        Mail sendMail = new Mail(
                draft.getUuid(),
                draft.getFrom(),
                parseMailAddresses(composeMailBean.getTo()),
                parseMailAddresses(composeMailBean.getCc()),
                parseMailAddresses(composeMailBean.getBcc()),
                composeMailBean.getSubject(),
                composeMailBean.getMessageText());

        sendMailService.sendMail(sendMail, user);

        return Mascherl
                .navigate(UriBuilder.fromMethod(MailInboxPage.class, "sent").build())
                .renderContainer("content")
                .withPageDef(
                        mailInboxPage.sent(1)
                                .container("messages", (model) -> model.put("successMsg", "Message sent!")));
    }

    @POST
    @Path("/mail/save/{mailUuid}")
    public MascherlAction saveOnExit(@PathParam("mailUuid") String mailUuid, @BeanParam ComposeMailBean composeMailBean) {
        Mail draft = new Mail(
                mailUuid,
                parseMailAddresses(composeMailBean.getTo()),
                parseMailAddresses(composeMailBean.getCc()),
                parseMailAddresses(composeMailBean.getBcc()),
                composeMailBean.getSubject(),
                composeMailBean.getMessageText());

        composeMailService.saveDraft(draft, user);

        return Mascherl
                .navigate("/mail")
                .renderContainer("content")
                .withPageDef(
                        mailInboxPage.inbox(1)
                                .container("messages", (model) -> model.put("infoMsg", "Draft saved!")));
    }

    private Set<MailAddress> parseMailAddresses(String mailAddressInput) {
        return Arrays.stream(mailAddressInput.split(",")).map(String::trim).filter((s) -> !s.isEmpty()).map(MailAddress::new).collect(Collectors.toSet());
    }


}
