package org.mascherl.example.page;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.User;
import org.mascherl.example.entity.MailEntity;
import org.mascherl.example.page.data.MailOverviewDto;
import org.mascherl.example.service.MailService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.session.MascherlSession;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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

    private static final int PAGE_SIZE = 50;
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
                .container("content", (model) -> calculateContainerModel(page, user, model));
    }

    private void calculateContainerModel(int page, User user, Model model) {
        model.put("mailCount", mailService.countMailsOfUser(user, MailEntity.MailType.RECEIVED));

        long unreadMailCount = mailService.countUnreadMailsOfUser(user, MailEntity.MailType.RECEIVED);
        if (unreadMailCount > 0) {
            model.put("unreadMailCount", unreadMailCount);
        }

        List<Mail> mails = mailService.getMailsForUser(user, MailEntity.MailType.RECEIVED, (page - 1) * PAGE_SIZE, PAGE_SIZE);
        model.put("mails", convertToPageModel(mails));
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
