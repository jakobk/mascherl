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
package org.mascherl.example.service;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailType;
import org.mascherl.example.domain.User;
import org.mascherl.example.entity.MailEntity;
import org.mascherl.example.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Service for sending mails.
 *
 * @author Jakob Korherr
 */
@Service
public class SendMailService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void sendMail(Mail mail, User currentUser) {
        if (!Objects.equals(currentUser.getEmail(), mail.getFrom().getAddress())) {
            throw new IllegalArgumentException("User can only send an email from his own address");
        }
        if (mail.getTo() == null || mail.getTo().isEmpty()) {
            throw new IllegalArgumentException("Receiver list cannot be empty");
        }

        ZonedDateTime sendTime = ZonedDateTime.now();

        MailEntity sendEntity;
        if (mail.getUuid() != null) {
            sendEntity = em.find(MailEntity.class, mail.getUuid());
            if (!Objects.equals(sendEntity.getUser().getUuid(), currentUser.getUuid())) {
                throw new IllegalArgumentException("The mail to be sent is not a draft of the current user.");
            }
            if (sendEntity.getMailType() != MailType.DRAFT) {
                throw new IllegalArgumentException("The mail to be sent needs to be a draft, but it is of type " + sendEntity.getMailType());
            }
            sendEntity.setMailType(MailType.SENT);
        } else {
            sendEntity = new MailEntity(MailType.SENT);
            sendEntity.setUser(em.getReference(UserEntity.class, currentUser.getUuid()));
        }
        sendEntity.setDateTime(sendTime);
        sendEntity.setUnread(false);
        sendEntity.setFrom(mail.getFrom());
        sendEntity.setTo(mail.getTo());
        sendEntity.setCc(mail.getCc());
        sendEntity.setBcc(mail.getBcc());
        sendEntity.setSubject(mail.getSubject());
        sendEntity.setMessageText(mail.getMessageText());
        em.persist(sendEntity);

        List<String> receiveUserUuids = findReceiveUserUuids(mail);
        for (String receiveUserUuid : receiveUserUuids) {
            MailEntity receiveEntity = new MailEntity(MailType.RECEIVED);
            receiveEntity.setUser(em.getReference(UserEntity.class, receiveUserUuid));
            receiveEntity.setDateTime(sendTime);
            receiveEntity.setUnread(true);
            receiveEntity.setFrom(mail.getFrom());
            receiveEntity.setTo(mail.getTo());
            receiveEntity.setCc(mail.getCc());
            receiveEntity.setBcc(mail.getBcc());
            receiveEntity.setSubject(mail.getSubject());
            receiveEntity.setMessageText(mail.getMessageText());
            em.persist(receiveEntity);
        }

        em.flush();
    }

    @Transactional
    public void sendMailFromSystem(Mail mail) {
        if (mail.getTo() == null || mail.getTo().isEmpty()) {
            throw new IllegalArgumentException("Receiver list cannot be empty");
        }

        ZonedDateTime sendTime = ZonedDateTime.now();

        List<String> receiveUserUuids = findReceiveUserUuids(mail);
        for (String receiveUserUuid : receiveUserUuids) {
            MailEntity receiveEntity = new MailEntity(MailType.RECEIVED);
            receiveEntity.setUser(em.getReference(UserEntity.class, receiveUserUuid));
            receiveEntity.setDateTime(sendTime);
            receiveEntity.setUnread(true);
            receiveEntity.setFrom(mail.getFrom());
            receiveEntity.setTo(mail.getTo());
            receiveEntity.setCc(mail.getCc());
            receiveEntity.setBcc(mail.getBcc());
            receiveEntity.setSubject(mail.getSubject());
            receiveEntity.setMessageText(mail.getMessageText());
            em.persist(receiveEntity);
        }

        em.flush();
    }

    private List<String> findReceiveUserUuids(Mail mail) {
        Set<String> mailReceiver = new HashSet<>();
        mail.getTo().forEach((address) -> mailReceiver.add(address.getAddress()));
        if (mail.getCc() != null) {
            mail.getCc().forEach((address) -> mailReceiver.add(address.getAddress()));
        }
        if (mail.getBcc() != null) {
            mail.getBcc().forEach((address) -> mailReceiver.add(address.getAddress()));
        }

        return em.createQuery(
                "select distinct user.uuid from UserEntity user " +
                        "where user.email in (:mailReceiver)", String.class)
                .setParameter("mailReceiver", mailReceiver)
                .getResultList();
    }

}
