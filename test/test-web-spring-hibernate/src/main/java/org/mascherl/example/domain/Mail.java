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
package org.mascherl.example.domain;

import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Mail domain class.
 *
 * @author Jakob Korherr
 */
public class Mail {

    private final String uuid;
    private final ZonedDateTime dateTime;
    private final MailType mailType;
    private final boolean isUnread;
    private final MailAddress from;
    private final Set<MailAddress> to;
    private final Set<MailAddress> cc;
    private final Set<MailAddress> bcc;
    private final String subject;
    private final String messageText;

    public Mail(String uuid, ZonedDateTime dateTime, MailType mailType, boolean isUnread,
                MailAddress from, Set<MailAddress> to, Set<MailAddress> cc, Set<MailAddress> bcc,
                String subject, String messageText) {
        this.uuid = uuid;
        this.dateTime = dateTime;
        this.mailType = mailType;
        this.isUnread = isUnread;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.messageText = messageText;
    }

    public Mail(MailAddress from, Set<MailAddress> to, Set<MailAddress> cc, Set<MailAddress> bcc,
                String subject, String messageText) {
        this.uuid = null;
        this.dateTime = null;
        this.mailType = MailType.DRAFT;
        this.isUnread = false;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.messageText = messageText;
    }

    public Mail(String uuid, Set<MailAddress> to, Set<MailAddress> cc, Set<MailAddress> bcc,
                String subject, String messageText) {
        this.uuid = uuid;
        this.dateTime = null;
        this.mailType = MailType.DRAFT;
        this.isUnread = false;
        this.from = null;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.messageText = messageText;
    }

    public Mail(String uuid, MailAddress from, Set<MailAddress> to, Set<MailAddress> cc, Set<MailAddress> bcc,
                String subject, String messageText) {
        this.uuid = uuid;
        this.dateTime = null;
        this.mailType = MailType.DRAFT;
        this.isUnread = false;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.messageText = messageText;
    }

    public String getUuid() {
        return uuid;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public MailType getMailType() {
        return mailType;
    }

    public boolean isUnread() {
        return isUnread;
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
