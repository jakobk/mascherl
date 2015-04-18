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

import org.hibernate.jpa.QueryHints;
import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.MailType;
import org.mascherl.example.domain.User;
import org.mascherl.example.entity.MailEntity;
import org.mascherl.example.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static org.mascherl.example.service.convert.MailConverter.convertToDomain;

/**
 * Service for composing mails.
 *
 * @author Jakob Korherr
 */
@Service
public class ComposeMailService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public String composeNewMail(User currentUser) {
        MailEntity mailEntity = new MailEntity(MailType.DRAFT);
        mailEntity.setUser(em.getReference(UserEntity.class, currentUser.getUuid()));
        mailEntity.setDateTime(ZonedDateTime.now());
        mailEntity.setUnread(false);
        mailEntity.setFrom(new MailAddress(currentUser.getEmail()));
        em.persist(mailEntity);
        em.flush();

        return mailEntity.getUuid();
    }

    public Mail openDraft(String uuid, User currentUser) {
        List<MailEntity> resultList = em.createQuery(
                "select m " +
                        "from MailEntity m " +
                        "where m.uuid = :uuid " +
                        "and m.user.uuid = :userUuid", MailEntity.class)
                .setParameter("uuid", uuid)
                .setParameter("userUuid", currentUser.getUuid())
                .setHint(QueryHints.HINT_READONLY, Boolean.TRUE)
                .getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        MailEntity entity = resultList.get(0);
        if (entity.getMailType() != MailType.DRAFT) {
            throw new IllegalStateException("Mail with uuid " + uuid + " + is not of type draft");
        }
        return convertToDomain(entity);
    }

    @Transactional
    public void saveDraft(Mail mail, User currentUser) {
        if (mail.getUuid() == null) {
            throw new IllegalArgumentException("Given draft mail has no UUID");
        }

        MailEntity draftEntity = em.find(MailEntity.class, mail.getUuid());
        if (!Objects.equals(draftEntity.getUser().getUuid(), currentUser.getUuid())) {
            throw new IllegalArgumentException("The draft to be saved does not belong to the current user.");
        }
        draftEntity.setMailType(MailType.DRAFT);
        draftEntity.setDateTime(ZonedDateTime.now());
        draftEntity.setUnread(false);
        draftEntity.setTo(mail.getTo());
        draftEntity.setCc(mail.getCc());
        draftEntity.setBcc(mail.getBcc());
        draftEntity.setSubject(mail.getSubject());
        draftEntity.setMessageText(mail.getMessageText());
        em.persist(draftEntity);
        em.flush();
    }

}
