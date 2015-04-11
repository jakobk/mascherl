package org.mascherl.example.page.data;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MailOverviewDto {

    private final String uuid;
    private final boolean isUnread;
    private final String fromTo;
    private final String subject;
    private final String dateTime;

    public MailOverviewDto(String uuid, boolean isUnread, String fromTo, String subject, String dateTime) {
        this.uuid = uuid;
        this.isUnread = isUnread;
        this.fromTo = fromTo;
        this.subject = subject;
        this.dateTime = dateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public String getFromTo() {
        return fromTo;
    }

    public String getSubject() {
        return subject;
    }

    public String getDateTime() {
        return dateTime;
    }
}
