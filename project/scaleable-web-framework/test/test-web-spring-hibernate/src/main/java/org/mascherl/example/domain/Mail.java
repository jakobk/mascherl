package org.mascherl.example.domain;

import java.util.Set;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class Mail {

    private final MailAddress from;
    private final Set<MailAddress> to;
    private final Set<MailAddress> cc;
    private final Set<MailAddress> bcc;
    private final String subject;
    private final String messageText;

    public Mail(MailAddress from, Set<MailAddress> to, Set<MailAddress> cc, Set<MailAddress> bcc, String subject, String messageText) {
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.messageText = messageText;
    }

    public MailAddress getFrom() {
        return from;
    }

    public Set<MailAddress> getTo() {
        return to;
    }

    public Set<MailAddress> getCc() {
        return cc;
    }

    public Set<MailAddress> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessageText() {
        return messageText;
    }
}
