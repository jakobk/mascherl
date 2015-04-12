package org.mascherl.example.service;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.entity.MailEntity;

/**
 * Converter for mail classes.
 *
 * @author Jakob Korherr
 */
class MailConverter {

    static Mail convertToDomain(MailEntity entity) {
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
