package org.mascherl.example.service;

import org.hibernate.jpa.QueryHints;
import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailType;
import org.mascherl.example.domain.User;
import org.mascherl.example.entity.MailEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for accessing a user's mails.
 *
 * @author Jakob Korherr
 */
@Service
public class MailService {

    @PersistenceContext
    private EntityManager em;

    public long countMailsOfUser(User currentUser, MailType mailType) {
        return em.createQuery(
                "select count(m.uuid) " +
                        "from MailEntity m " +
                        "where m.user.uuid = :userUuid " +
                        "and m.mailType = :mailType", Long.class)
                .setParameter("userUuid", currentUser.getUuid())
                .setParameter("mailType", mailType)
                .getSingleResult();
    }

    public long countUnreadMailsOfUser(User currentUser, MailType mailType) {
        return em.createQuery(
                "select count(m.uuid) " +
                        "from MailEntity m " +
                        "where m.user.uuid = :userUuid " +
                        "and m.mailType = :mailType " +
                        "and m.isUnread = true", Long.class)
                .setParameter("userUuid", currentUser.getUuid())
                .setParameter("mailType", mailType)
                .getSingleResult();
    }

    public List<Mail> getMailsForUser(User currentUser, MailType mailType, int offset, int pageSize) {
        List<MailEntity> resultList = em.createQuery(
                "select m " +
                        "from MailEntity m " +
                        "where m.user.uuid = :userUuid " +
                        "and m.mailType = :mailType " +
                        "order by m.dateTime desc", MailEntity.class)
                .setParameter("userUuid", currentUser.getUuid())
                .setParameter("mailType", mailType)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .setHint(QueryHints.HINT_READONLY, Boolean.TRUE)
                .setHint(QueryHints.HINT_FETCH_SIZE, pageSize)
                .getResultList();

        return resultList.stream().map(this::convertToDomain).collect(Collectors.toList());
    }

    @Transactional
    public Mail readMail(String uuid, User currentUser) {
        List<MailEntity> resultList = em.createQuery(
                "select m " +
                        "from MailEntity m " +
                        "where m.uuid = :uuid " +
                        "and m.user.uuid = :userUuid ", MailEntity.class)
                .setParameter("uuid", uuid)
                .setParameter("userUuid", currentUser.getUuid())
                .getResultList();
        if (resultList.isEmpty()) {
            throw new IllegalArgumentException("Mail with uuid " + uuid + " does not exist for user " + currentUser);
        }

        MailEntity mailEntity = resultList.get(0);
        if (mailEntity.isUnread()) {
            mailEntity.setUnread(false);
            em.persist(mailEntity);
            em.flush();
        }

        return convertToDomain(mailEntity);
    }

    @Transactional
    public void moveToTrash(List<String> uuids, User currentUser) {
        em.createQuery(
                "update MailEntity m " +
                        "set m.mailType = :mailTypeTrash " +
                        "where m.uuid in (:uuids) " +
                        "and m.user.uuid = :userUuid ")
                .setParameter("uuids", uuids)
                .setParameter("userUuid", currentUser.getUuid())
                .setParameter("mailTypeTrash", MailType.TRASH)
                .executeUpdate();
    }

    @Transactional
    public void permanentlyDeleteTrashMails(List<String> uuids, User currentUser) {
        em.createQuery(
                "delete from MailEntity m " +
                        "where m.uuid in (:uuids) " +
                        "and m.user.uuid = :userUuid " +
                        "and m.mailType = :mailTypeTrash")
                .setParameter("uuids", uuids)
                .setParameter("userUuid", currentUser.getUuid())
                .setParameter("mailTypeTrash", MailType.TRASH)
                .executeUpdate();
    }

    private Mail convertToDomain(MailEntity entity) {
        return new Mail(
                entity.getUuid(),
                entity.getDateTime(),
                entity.getMailType(),
                entity.isUnread(),
                entity.getFrom(),
                entity.getTo(),
                entity.getCc(),
                entity.getBcc(),
                entity.getSubject(),
                entity.getMessageText());
    }

}
