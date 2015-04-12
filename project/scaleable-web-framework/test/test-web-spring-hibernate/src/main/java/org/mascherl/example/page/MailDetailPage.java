package org.mascherl.example.page;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.MailType;
import org.mascherl.example.domain.User;
import org.mascherl.example.page.data.MailDetailDto;
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
import java.util.stream.Collectors;

import static org.mascherl.example.page.format.DateTimeFormat.formatDateTime;

/**
 * TODO
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

        return Mascherl.page("/templates/mailDetail.html")
                .pageTitle("WebMail powered by Mascherl")
                .container("userInfo", (model) -> model.put("user", user))
                .container("content", (model) -> calculateContainerModel(mailUuid, user, model));
    }

    private void calculateContainerModel(String mailUuid, User user, Model model) {
        Mail mail = mailService.readMail(mailUuid, user);
        model.put("mail", convertToPageModel(mail));
    }

    private MailDetailDto convertToPageModel(Mail mail) {
        return new MailDetailDto(
                mail.getUuid(),
                mail.getFrom().getAddress(),
                mail.getTo().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")),
                mail.getCc().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")),
                mail.getBcc().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")),
                mail.getSubject(),
                mail.getMessageText(),
                formatDateTime(mail.getDateTime()),
                calculateDateTimeLabel(mail.getMailType()));
    }

    private String calculateDateTimeLabel(MailType mailType) {
        switch (mailType) {
            case RECEIVED: return "received";
            case SENT:     return "sent";
            case DRAFT:    return "created";
            case TRASH:    return "time";  // TODO trash mails need to be handled differently.
            default:       throw new IllegalArgumentException("Illegal MailType: " + mailType);
        }
    }

}
