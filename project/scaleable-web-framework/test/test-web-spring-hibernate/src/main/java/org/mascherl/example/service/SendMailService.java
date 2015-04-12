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
            throw new IllegalArgumentException("User can only send email from his own address");
        }
        if (mail.getTo() == null || mail.getTo().isEmpty()) {
            throw new IllegalArgumentException("Receiver list cannot be empty");
        }

        ZonedDateTime sendTime = ZonedDateTime.now();

        MailEntity sendEntity = new MailEntity(MailType.SENT);
        sendEntity.setUser(em.getReference(UserEntity.class, currentUser.getUuid()));
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
