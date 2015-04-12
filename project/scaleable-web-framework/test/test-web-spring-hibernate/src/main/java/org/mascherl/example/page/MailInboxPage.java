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
import java.util.List;
import java.util.stream.Collectors;

import static org.mascherl.example.page.PageUtils.getCurrentUser;
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
    private MailService mailService;

    @GET
    @Path("/mail")
    public MascherlPage inbox(@QueryParam("page") @DefaultValue("1") int page) {
        User user = getCurrentUser();
        return mailInboxBasePage(user)
                .pageTitle("Inbox - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.RECEIVED));
    }

    @GET
    @Path("/mail/sent")
    public MascherlPage sent(@QueryParam("page") @DefaultValue("1") int page) {
        User user = getCurrentUser();
        return mailInboxBasePage(user)
                .pageTitle("Sent mails - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.SENT));
    }

    @GET
    @Path("/mail/draft")
    public MascherlPage draft(@QueryParam("page") @DefaultValue("1") int page) {
        User user = getCurrentUser();
        return mailInboxBasePage(user)
                .pageTitle("Drafts - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.DRAFT));
    }

    @GET
    @Path("/mail/trash")
    public MascherlPage trash(@QueryParam("page") @DefaultValue("1") int page) {
        User user = getCurrentUser();
        return mailInboxBasePage(user)
                .pageTitle("Trash - WebMail powered by Mascherl")
                .container("pageContent", (model) -> populateModelWithMailData(page, user, model, MailType.TRASH));
    }

    @POST
    @Path("/mail/delete")
    public MascherlAction delete(
            @FormParam("mailUuid") List<String> uuids,
            @FormParam("page") @DefaultValue("1") int page,
            @FormParam("mailType") @DefaultValue("RECEIVED") MailType mailType) {
        User user = getCurrentUser();

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

    private void populateModelWithMailData(int page, User user, Model model, MailType mailType) {
        model.put("mailCount", mailService.countMailsOfUser(user, mailType));

        List<Mail> mails = mailService.getMailsForUser(user, mailType, (page - 1) * PAGE_SIZE, PAGE_SIZE);
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
