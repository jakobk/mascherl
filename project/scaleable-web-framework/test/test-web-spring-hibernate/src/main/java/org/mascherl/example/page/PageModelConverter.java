package org.mascherl.example.page;

import com.github.mustachejava.util.HtmlEscaper;
import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.MailType;
import org.mascherl.example.page.data.MailDetailDto;

import java.io.StringWriter;
import java.util.stream.Collectors;

import static org.mascherl.example.page.format.DateTimeFormat.formatDateTime;

/**
 * Convert for domain objects to page model objects.
 *
 * @author Jakob Korherr
 */
public class PageModelConverter {

    private static final String LINE_FEED_HTML_ESCAPED = "&#10;";
    private static final String HTML_BR = "<br/>";

    public static MailDetailDto convertToPageModelForView(Mail mail) {
        return convertToPageModel(mail, true);
    }

    public static MailDetailDto convertToPageModelForEdit(Mail mail) {
        return convertToPageModel(mail, false);
    }

    private static MailDetailDto convertToPageModel(Mail mail, boolean newLineToBr) {
        String messageText = escapeHtml(mail.getMessageText());
        if (newLineToBr) {
            messageText = replaceNewlineWithBrTag(messageText);
        }
        return new MailDetailDto(
                mail.getUuid(),
                mail.getMailType().name(),
                mail.getFrom().getAddress(),
                mail.getTo().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")),
                mail.getCc().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")),
                mail.getBcc().stream().map(MailAddress::getAddress).collect(Collectors.joining(", ")),
                mail.getSubject(),
                messageText,
                formatDateTime(mail.getDateTime()),
                calculateDateTimeLabel(mail.getMailType()));
    }

    public static String escapeHtml(String toEscape) {
        if (toEscape == null) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        HtmlEscaper.escape(toEscape, stringWriter, true);
        return stringWriter.toString();
    }

    public static String replaceNewlineWithBrTag(String text) {
        if (text == null) {
            return null;
        }
        return text.replace(LINE_FEED_HTML_ESCAPED, LINE_FEED_HTML_ESCAPED + HTML_BR);
    }

    public static String calculateDateTimeLabel(MailType mailType) {
        switch (mailType) {
            case RECEIVED: return "received";
            case SENT:     return "sent";
            case DRAFT:    return "created";
            case TRASH:    return "time";
            default:       throw new IllegalArgumentException("Illegal MailType: " + mailType);
        }
    }

}
