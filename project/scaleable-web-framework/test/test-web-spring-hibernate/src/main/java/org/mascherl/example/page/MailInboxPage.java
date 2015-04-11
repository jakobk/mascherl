package org.mascherl.example.page;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.User;
import org.mascherl.example.entity.MailEntity;
import org.mascherl.example.page.data.MailOverviewDto;
import org.mascherl.example.service.MailService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.session.MascherlSession;
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

import static org.mascherl.example.page.format.DateTimeFormat.formatDateTime;
import static org.mascherl.example.page.format.StringFormat.truncate;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Component
public class MailInboxPage {

    private static final int PAGE_SIZE = 20;
    private static final int FROM_TO_MAX_LENGTH = 100;
    private static final int SUBJECT_MAX_LENGTH = 200;

    @Inject
    private MailService mailService;

    @GET
    @Path("/mail")
    public MascherlPage inbox(@QueryParam("page") @DefaultValue("1") int page) {
        MascherlSession session = MascherlSession.getInstance();
        User user = session.get("user", User.class);

        return Mascherl.page("/templates/mailInbox.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("mailTypeNav", (model) -> populateMailTypeNavModel(user, model))
                .container("content", (model) -> populateModelWithMailData(page, user, model, MailEntity.MailType.RECEIVED));
    }

    @GET
    @Path("/mail/sent")
    public MascherlPage sent(@QueryParam("page") @DefaultValue("1") int page) {
        MascherlSession session = MascherlSession.getInstance();
        User user = session.get("user", User.class);

        return Mascherl.page("/templates/mailSent.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("mailTypeNav", (model) -> populateMailTypeNavModel(user, model))
                .container("content", (model) -> populateModelWithMailData(page, user, model, MailEntity.MailType.SENT));
    }

    @GET
    @Path("/mail/draft")
    public MascherlPage draft(@QueryParam("page") @DefaultValue("1") int page) {
        MascherlSession session = MascherlSession.getInstance();
        User user = session.get("user", User.class);

        return Mascherl.page("/templates/mailDraft.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("mailTypeNav", (model) -> populateMailTypeNavModel(user, model))
                .container("content", (model) -> populateModelWithMailData(page, user, model, MailEntity.MailType.DRAFT));
    }

    @GET
    @Path("/mail/trash")
    public MascherlPage trash(@QueryParam("page") @DefaultValue("1") int page) {
        MascherlSession session = MascherlSession.getInstance();
        User user = session.get("user", User.class);

        return Mascherl.page("/templates/mailTrash.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("mailTypeNav", (model) -> populateMailTypeNavModel(user, model))
                .container("content", (model) -> populateModelWithMailData(page, user, model, MailEntity.MailType.TRASH));
    }

    @POST
    @Path("/mail/moveToTrash")
    public MascherlAction moveToTrash(
            @FormParam("mailUuid") List<String> uuids,
            @FormParam("page") @DefaultValue("1") int page) {
        MascherlSession session = MascherlSession.getInstance();
        User user = session.get("user", User.class);

        if (!uuids.isEmpty()) {
            mailService.moveToTrash(uuids, user);
        }

        return Mascherl.stay().renderAll().withPageDef(
                inbox(page)   // TODO not always inbox
                        .container("main", (model) -> {
                            if (uuids.isEmpty()) {
                                model.put("errorMsg", "No mails selected.");
                            } else {
                                model.put("successMsg", uuids.size() + " mails moved to trash.");
                            }
                        }));
    }

    private void populateModelWithMailData(int page, User user, Model model, MailEntity.MailType mailType) {
        model.put("mailCount", mailService.countMailsOfUser(user, mailType));

        List<Mail> mails = mailService.getMailsForUser(user, mailType, (page - 1) * PAGE_SIZE, PAGE_SIZE);
        model.put("mails", convertToPageModel(mails));
    }

    private void populateMailTypeNavModel(User user, Model model) {
        long unreadMailCount = mailService.countUnreadMailsOfUser(user, MailEntity.MailType.RECEIVED);
        if (unreadMailCount > 0) {
            model.put("unreadInboxMailCount", unreadMailCount);
        }
    }

    private List<MailOverviewDto> convertToPageModel(List<Mail> mails) {
        return mails.stream().map((mail) -> new MailOverviewDto(
                mail.getUuid(),
                mail.isUnread(),
                truncate(mail.getFrom().getAddress(), FROM_TO_MAX_LENGTH),
                truncate(mail.getSubject(), SUBJECT_MAX_LENGTH),
                formatDateTime(mail.getDateTime())
        )).collect(Collectors.toList());
    }


    // TODO for send page
    // String to = truncate(mail.getTo().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")), TO_MAX_LENGTH);



}
