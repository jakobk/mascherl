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
package org.mascherl.example.service.convert;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.entity.MailEntity;

/**
 * Converter for mail classes.
 *
 * @author Jakob Korherr
 */
public class MailConverter {

    public static Mail convertToDomain(MailEntity entity) {
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