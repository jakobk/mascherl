package org.mascherl.example.page.data;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MailDetailDto {

    private final String uuid;
    private final String mailType;
    private final String from;
    private final String to;
    private final String cc;
    private final String bcc;
    private final String subject;
    private final String messageText;
    private final String dateTime;
    private final String dateTimeLabel;

    public MailDetailDto(String uuid, String mailType, String from, String to, String cc, String bcc, String subject, String messageText, String dateTime, String dateTimeLabel) {
        this.uuid = uuid;
        this.mailType = mailType;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.messageText = messageText;
        this.dateTime = dateTime;
        this.dateTimeLabel = dateTimeLabel;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMailType() {
        return mailType;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCc() {
        return cc;
    }

    public String getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDateTimeLabel() {
        return dateTimeLabel;
    }
}
