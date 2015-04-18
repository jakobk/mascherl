package org.mascherl.example.page;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.User;
import org.mascherl.example.page.data.ComposeMailBean;
import org.mascherl.example.service.ComposeMailService;
import org.mascherl.example.service.ComposeMailServiceAsync;
import org.mascherl.example.service.SendMailServiceAsync;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.mascherl.session.MascherlSession;
import org.mascherl.validation.ValidationResult;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mascherl.example.page.PageModelConverter.convertToPageModelForEdit;
import static org.mascherl.example.page.PageUtils.determineReturnToPage;
import static org.mascherl.example.page.PageUtils.getValidationErrorMessages;
import static org.mascherl.example.page.PageUtils.parsePageParameter;

/**
 * Page class for the mail composing page.
 *
 * @author Jakob Korherr
 */
@Component
public class MailComposePage {

    @Inject
    private ValidationResult validationResult;

    @Inject
    private User user;

    @Inject
    private MailInboxPage mailInboxPage;

    @Inject
    private MailDetailPage mailDetailPage;

    @Inject
    private ComposeMailService composeMailService;

    @Inject
    private ComposeMailServiceAsync composeMailServiceAsync;

    @Inject
    private SendMailServiceAsync sendMailService;

    @GET
    @Path("/mail/compose/{mailUuid}")
    public MascherlPage compose(@PathParam("mailUuid") String mailUuid) {
        try {
            return Mascherl.page("/templates/mail/mailCompose.html")
                    .pageTitle("Compose - WebMail powered by Mascherl")
                    .container("userInfo", (model) -> model.put("user", user))
                    .container("pageContent", (model) -> {
                        Mail mail = composeMailService.openDraft(mailUuid, user);
                        if (mail != null) {
                            model.put("mail", convertToPageModelForEdit(mail));
                        }
                    });
        } catch (IllegalStateException e) {
            return mailDetailPage.mailDetail(mailUuid)
                    .replaceUrl(UriBuilder.fromMethod(MailDetailPage.class, "mailDetail").build(mailUuid));
        }
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
    public Observable<MascherlAction> send(
            @PathParam("mailUuid") String mailUuid,
            @Valid @ConvertGroup(from = Default.class, to = ComposeMailBean.Send.class) @BeanParam ComposeMailBean composeMailBean) {
        if (!validationResult.isValid()) {
            return Observable.just(Mascherl
                    .stay()
                    .renderContainer("messages")
                    .withPageDef(compose(mailUuid)
                            .container("messages", (model) -> model.put("errorMsg", getValidationErrorMessages(validationResult)))));
        }

        User localUser = MascherlSession.getInstance().get("user", User.class);
        return composeMailServiceAsync.openDraft(mailUuid, localUser)
                .map((draft) -> new Mail(
                        draft.getUuid(),
                        draft.getFrom(),
                        parseMailAddresses(composeMailBean.getTo()),
                        parseMailAddresses(composeMailBean.getCc()),
                        parseMailAddresses(composeMailBean.getBcc()),
                        composeMailBean.getSubject(),
                        composeMailBean.getMessageText()))
                .flatMap((sendMail) -> sendMailService.sendMail(sendMail, localUser))
                .timeout(10, TimeUnit.SECONDS)
                .map((voidResult) ->
                                Mascherl.deferredAction(() ->
                                        Mascherl
                                                .navigate(UriBuilder.fromMethod(MailInboxPage.class, "sent").build())
                                                .renderContainer("content")
                                                .withPageDef(
                                                        mailInboxPage.sent(1)
                                                                .container("messages", (model) -> model.put("successMsg", "Message sent!"))))
                );
    }

    @POST
    @Path("/mail/save/{mailUuid}")
    public MascherlAction saveOnExit(
            @PathParam("mailUuid") String mailUuid,
            @BeanParam ComposeMailBean composeMailBean,
            @FormParam("returnTo") @DefaultValue("/mail") String returnTo) {
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
                        determineReturnToPage(mailInboxPage, returnTo, parsePageParameter(returnTo))
                                .container("messages", (model) -> model.put("infoMsg", "Draft saved!")));
    }

    private Set<MailAddress> parseMailAddresses(String mailAddressInput) {
        return Arrays.stream(mailAddressInput.split(",")).map(String::trim).filter((s) -> !s.isEmpty()).map(MailAddress::new).collect(Collectors.toSet());
    }


}
