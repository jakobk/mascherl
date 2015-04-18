package org.mascherl.example.page.data;

/**
 * DTO for displaying an overview of a mail.
 *
 * @author Jakob Korherr
 */
public class MailOverviewDto {

    private final String uuid;
    private final boolean isUnread;
    private final String from;
    private final String to;
    private final String subject;
    private final String dateTime;

    public MailOverviewDto(String uuid, boolean isUnread, String from, String to, String subject, String dateTime) {
        this.uuid = uuid;
        this.isUnread = isUnread;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.dateTime = dateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getDateTime() {
        return dateTime;
    }
}
