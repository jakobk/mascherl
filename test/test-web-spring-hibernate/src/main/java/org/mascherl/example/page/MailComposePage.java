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

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.MailAddressUsage;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    private static final int RECEIVER_HINT_MAX_ADDRESSES = 3;

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
    private SendMailServiceAsync sendMailServiceAsync;

    @GET
    @Path("/mail/compose/{mailUuid}")
    public Observable<MascherlPage> compose(@PathParam("mailUuid") String mailUuid) {
        User localUser = MascherlSession.getInstance().get("user", User.class);

        Observable<List<MailAddressUsage>> sendToAddressesObservable =
                composeMailServiceAsync.getLastSendToAddresses(localUser, RECEIVER_HINT_MAX_ADDRESSES)
                        .timeout(500, TimeUnit.MILLISECONDS, Observable.just(Collections.emptyList()))
                        .onErrorReturn((throwable) -> Collections.emptyList());
        Observable<List<MailAddressUsage>> receivedAddressesObservable =
                composeMailServiceAsync.getLastReceivedFromAddresses(localUser, RECEIVER_HINT_MAX_ADDRESSES)
                        .timeout(500, TimeUnit.MILLISECONDS, Observable.just(Collections.emptyList()))
                        .onErrorReturn((throwable) -> Collections.emptyList());

        return sendToAddressesObservable
                .zipWith(
                        receivedAddressesObservable,
                        (sendToList, receivedFromList) -> {
                            List<MailAddressUsage> addresses = new ArrayList<>(RECEIVER_HINT_MAX_ADDRESSES * 2);
                            if (receivedFromList != null) {
                                addresses.addAll(receivedFromList);
                            }
                            if (sendToList != null) {
                                addresses.addAll(sendToList);
                            }
                            return addresses.stream()
                                    .distinct()
                                    .sorted((u1, u2) -> u2.getDateTime().compareTo(u1.getDateTime()))
                                    .limit(RECEIVER_HINT_MAX_ADDRESSES)
                                    .collect(Collectors.toList());
                        }
                )
                .zipWith(
                        composeMailServiceAsync.openDraft(mailUuid, localUser),
                        (List<MailAddressUsage> receiverHintList, Mail mail) ->
                                Mascherl.page("/templates/mail/mailCompose.html")
                                        .pageTitle("Compose - WebMail powered by Mascherl")
                                        .container("userInfo", (model) -> model.put("user", localUser))
                                        .container("pageContent", (model) -> {
                                            if (mail != null) {
                                                model.put("mail", convertToPageModelForEdit(mail));
                                            }

                                            String receiverHint = receiverHintList.stream()
                                                    .map((usage) -> usage.getMailAddress().getAddress())
                                                    .collect(Collectors.joining(", "));
                                            if (!receiverHint.isEmpty()) {
                                                model.put("receiverHint", receiverHint);
                                            }
                                        })
                )
                .onErrorReturn((throwable) -> {
                    if (throwable instanceof IllegalStateException) {
                        return Mascherl.deferredPage(() ->
                                mailDetailPage.mailDetail(mailUuid)
                                        .replaceUrl(UriBuilder.fromMethod(MailDetailPage.class, "mailDetail").build(mailUuid))
                                        .pageGroup("MailDetailPage"));
                    }
                    throw (RuntimeException) throwable;
                });
    }

    @POST
    @Path("/mail/compose")
    public Observable<MascherlAction> composeNew() {
        String mailUuid = composeMailService.composeNewMail(user);
        return compose(mailUuid)
                .map((pageDef) -> Mascherl
                        .navigate(UriBuilder.fromMethod(getClass(), "compose").build(mailUuid))
                        .renderContainer("content")
                        .withPageDef(pageDef));
    }

    @POST
    @Path("/mail/send/{mailUuid}")
    public Observable<MascherlAction> send(
            @PathParam("mailUuid") String mailUuid,
            @Valid @ConvertGroup(from = Default.class, to = ComposeMailBean.Send.class) @BeanParam ComposeMailBean composeMailBean) {
        if (!validationResult.isValid()) {
            List<String> validationErrorMessages = getValidationErrorMessages(validationResult);
            return compose(mailUuid)
                    .map((pageDef) -> Mascherl
                            .stay()
                            .renderContainer("messages")
                            .withPageDef(pageDef
                                    .container("messages", (model) -> model.put("errorMsg", validationErrorMessages))));
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
                .flatMap((sendMail) -> sendMailServiceAsync.sendMail(sendMail, localUser))
                .timeout(10, TimeUnit.SECONDS)
                .map((voidResult) ->
                                Mascherl.deferredAction(() ->
                                        Mascherl
                                                .navigate(UriBuilder.fromMethod(MailInboxPage.class, "sent").build())
                                                .renderContainer("content")
                                                .withPageDef(
                                                        mailInboxPage.sent(1)
                                                                .container("messages", (model) -> model.put("successMsg", "Message sent!")))
                                                .withPageGroup("MailInboxPage")
                                )
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
                .navigate(returnTo)
                .renderContainer("content")
                .withPageDef(
                        determineReturnToPage(mailInboxPage, returnTo, parsePageParameter(returnTo))
                                .container("messages", (model) -> model.put("infoMsg", "Draft saved!")))
                .withPageGroup("MailInboxPage");
    }

    private Set<MailAddress> parseMailAddresses(String mailAddressInput) {
        return Arrays.stream(mailAddressInput.split(",")).map(String::trim).filter((s) -> !s.isEmpty()).map(MailAddress::new).collect(Collectors.toSet());
    }

}
