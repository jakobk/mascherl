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
import org.mascherl.example.domain.MailType;
import org.mascherl.example.domain.User;
import org.mascherl.example.page.data.MailOverviewDto;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

import static org.mascherl.example.page.format.DateTimeFormat.formatDateTime;
import static org.mascherl.example.page.format.StringFormat.pluralize;
import static org.mascherl.example.page.format.StringFormat.truncate;

/**
 * Page class for the inbox overview pages (inbox, sent mails, drafts, trash).
 *
 * @author Jakob Korherr
 */
@Component
public class MailInboxPage {

    private static final int PAGE_SIZE = 20;
    private static final int FROM_TO_MAX_LENGTH = 60;
    private static final int SUBJECT_MAX_LENGTH = 60;

    @Inject
    private User user;

    @Inject
    private MailService mailService;

    @GET
    @Path("/mail")
    public MascherlPage inbox(@QueryParam("page") @DefaultValue("1") int pageParam) {
        long mailCount = mailService.countMailsOfUser(user, MailType.RECEIVED);
        int page = calculateMaxPage(pageParam, mailCount);

        MascherlPage pageDef = mailInboxBasePage(user)
                .pageTitle("Inbox - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.RECEIVED, mailCount));
        adjustPageUriIfNecessary(pageParam, page, pageDef, "/mail");
        return pageDef;
    }

    @GET
    @Path("/mail/sent")
    public MascherlPage sent(@QueryParam("page") @DefaultValue("1") int pageParam) {
        long mailCount = mailService.countMailsOfUser(user, MailType.SENT);
        int page = calculateMaxPage(pageParam, mailCount);

        MascherlPage pageDef = mailInboxBasePage(user)
                .pageTitle("Sent mails - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.SENT, mailCount));
        adjustPageUriIfNecessary(pageParam, page, pageDef, "/mail/sent");
        return pageDef;
    }

    @GET
    @Path("/mail/draft")
    public MascherlPage draft(@QueryParam("page") @DefaultValue("1") int pageParam) {
        long mailCount = mailService.countMailsOfUser(user, MailType.DRAFT);
        int page = calculateMaxPage(pageParam, mailCount);

        MascherlPage pageDef = mailInboxBasePage(user)
                .pageTitle("Drafts - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.DRAFT, mailCount));
        adjustPageUriIfNecessary(pageParam, page, pageDef, "/mail/draft");
        return pageDef;
    }

    @GET
    @Path("/mail/trash")
    public MascherlPage trash(@QueryParam("page") @DefaultValue("1") int pageParam) {
        long mailCount = mailService.countMailsOfUser(user, MailType.TRASH);
        int page = calculateMaxPage(pageParam, mailCount);

        MascherlPage pageDef = mailInboxBasePage(user)
                .pageTitle("Trash - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.TRASH, mailCount));
        adjustPageUriIfNecessary(pageParam, page, pageDef, "/mail/trash");
        return pageDef;
    }

    @POST
    @Path("/mail/delete")
    public MascherlAction delete(
            @FormParam("mailUuid") List<String> uuids,
            @FormParam("page") @DefaultValue("1") int page,
            @FormParam("mailType") @DefaultValue("RECEIVED") MailType mailType) {
        String whatWeDid;
        if (!uuids.isEmpty()) {
            if (mailType == MailType.TRASH) {
                mailService.permanentlyDeleteTrashMails(uuids, user);
                whatWeDid = "permanently deleted";
            } else {
                mailService.moveToTrash(uuids, user);
                whatWeDid = "moved to trash";
            }
        } else {
            whatWeDid = null;
        }

        return Mascherl.stay().renderContainer("content").withPageDef(
                createPageForMailType(mailType, page)
                        .container("messages", (model) -> {
                            if (uuids.isEmpty()) {
                                model.put("errorMsg", "No mails selected.");
                            } else {
                                model.put("successMsg", uuids.size() + " " + pluralize("mail", uuids) + " " + whatWeDid + ".");
                            }
                        }));
    }

    private MascherlPage mailInboxBasePage(User user) {
        return Mascherl.page("/templates/mail/mailInbox.html")
                .container("userInfo", (model) -> model.put("user", user));
    }

    private MascherlPage createPageForMailType(MailType mailType, int page) {
        switch (mailType) {
            case RECEIVED: return inbox(page);
            case SENT:     return sent(page);
            case DRAFT:    return draft(page);
            case TRASH:    return trash(page);
            default:       throw new IllegalArgumentException("Illegal MailType: " + mailType);
        }
    }

    private void populateModelWithMailData(int page, User user, Model model, MailType mailType, long mailCount) {
        model.put("mailCount", mailCount);

        List<Mail> mails = mailService.getMailsForUser(user, mailType, calculateOffset(page), PAGE_SIZE);
        model.put("mails", convertToPageModel(mails));

        model.put("mailType", mailType.name());

        long unreadMailCount = mailService.countUnreadMailsOfUser(user, MailType.RECEIVED);
        if (unreadMailCount > 0) {
            model.put("unreadInboxMailCount", unreadMailCount);
        }

        long draftMailCount = mailService.countMailsOfUser(user, MailType.DRAFT);
        if (draftMailCount > 0) {
            model.put("draftMailCount", draftMailCount);
        }

        if (mailType == MailType.DRAFT) {
            model.put("mailDetailLink", "/mail/compose");
        } else {
            model.put("mailDetailLink", "/mail");
        }

        switch (mailType) {
            case RECEIVED:
                model.put("inboxPage", true);
                model.put("showFrom", true);
                break;
            case SENT:
                model.put("sentPage", true);
                model.put("showTo", true);
                break;
            case DRAFT:
                model.put("draftPage", true);
                model.put("showTo", true);
                break;
            case TRASH:
                model.put("trashPage", true);
                model.put("showFrom", true);
                model.put("showTo", true);
                break;
            default: throw new IllegalArgumentException("Illegal MailType: " + mailType);
        }
    }

    private int calculateOffset(int page) {
        return (page - 1) * PAGE_SIZE;
    }

    private int calculateMaxPage(int page, long mailCount) {
        if (calculateOffset(page) > mailCount) {
            page = ((int) (mailCount / PAGE_SIZE)) + 1;
        }
        return page;
    }

    private void adjustPageUriIfNecessary(int pageParam, int page, MascherlPage pageDef, String uriTemplate) {
        if (page != pageParam) {
            UriBuilder uri = UriBuilder.fromUri(uriTemplate);
            if (page > 1) {
                uri.queryParam("page", page);
            }
            pageDef.replaceUrl(uri.build());
        }
    }

    private List<MailOverviewDto> convertToPageModel(List<Mail> mails) {
        return mails.stream().map((mail) -> {
            // need to put this into a separate variable first, because of a JDK bug..
            String to = truncate(mail.getTo().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")), FROM_TO_MAX_LENGTH);
            return new MailOverviewDto(
                    mail.getUuid(),
                    mail.isUnread(),
                    truncate(mail.getFrom().getAddress(), FROM_TO_MAX_LENGTH),
                    to,
                    truncate(mail.getSubject(), SUBJECT_MAX_LENGTH),
                    formatDateTime(mail.getDateTime())
            );
        }).collect(Collectors.toList());
    }

}
