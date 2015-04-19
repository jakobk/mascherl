/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.example.entity;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.MailType;

import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Entity for mails.
 *
 * @author Jakob Korherr
 */
@Entity
@Table(name = "mail")
public class MailEntity extends BaseEntity {

    @Column(name = "mail_type")
    @Enumerated(EnumType.STRING)
    private MailType mailType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    @BatchSize(size = 100)
    private UserEntity user;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @Column(name = "datetime")
    private ZonedDateTime dateTime;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "is_unread", nullable = false)
    private boolean isUnread;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "from_address", length = 255))
    private MailAddress from;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = MailAddress.class)
    @CollectionTable(
            name = "mail_to",
            joinColumns = @JoinColumn(name = "mail_uuid")
    )
    @BatchSize(size = 100)
    private Set<MailAddress> to;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = MailAddress.class)
    @CollectionTable(
            name = "mail_cc",
            joinColumns = @JoinColumn(name = "mail_uuid")
    )
    @BatchSize(size = 100)
    private Set<MailAddress> cc;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = MailAddress.class)
    @CollectionTable(
            name = "mail_bcc",
            joinColumns = @JoinColumn(name = "mail_uuid")
    )
    @BatchSize(size = 100)
    private Set<MailAddress> bcc;

    @Column(name = "subject", length = 255)
    private String subject;

    @Lob
    @Column(name = "message_text", length = Integer.MAX_VALUE)
    private String messageText;

    protected MailEntity() {}

    public MailEntity(MailType mailType) {
        this.mailType = mailType;
    }

    public MailType getMailType() {
        return mailType;
    }

    public void setMailType(MailType mailType) {
        this.mailType = mailType;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean isUnread) {
        this.isUnread = isUnread;
    }

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
