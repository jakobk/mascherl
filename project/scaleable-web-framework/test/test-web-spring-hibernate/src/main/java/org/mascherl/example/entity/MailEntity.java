package org.mascherl.example.entity;

import org.mascherl.example.domain.MailAddress;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Set;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Entity
@Table(name = "mail")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class MailEntity extends BaseEntity {

    @Embedded
    private MailAddress from;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = MailAddress.class)
    @CollectionTable(
            name = "mail_to",
            joinColumns = @JoinColumn(name = "mail_uuid")
    )
    private Set<MailAddress> to;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = MailAddress.class)
    @CollectionTable(
            name = "mail_cc",
            joinColumns = @JoinColumn(name = "mail_uuid")
    )
    private Set<MailAddress> cc;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = MailAddress.class)
    @CollectionTable(
            name = "mail_bcc",
            joinColumns = @JoinColumn(name = "mail_uuid")
    )
    private Set<MailAddress> bcc;

    @Column(name = "subject", length = 255)
    private String subject;

    @Lob
    @Column(name = "message_text")
    private String messageText;

    protected MailEntity() { }

    public MailAddress getFrom() {
        return from;
    }

    public void setFrom(MailAddress from) {
        this.from = from;
    }

    public Set<MailAddress> getTo() {
        return to;
    }

    public void setTo(Set<MailAddress> to) {
        this.to = to;
    }

    public Set<MailAddress> getCc() {
        return cc;
    }

    public void setCc(Set<MailAddress> cc) {
        this.cc = cc;
    }

    public Set<MailAddress> getBcc() {
        return bcc;
    }

    public void setBcc(Set<MailAddress> bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
