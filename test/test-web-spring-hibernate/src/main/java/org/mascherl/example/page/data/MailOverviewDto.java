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
package org.mascherl.example.page.data;

/**
 * DTO for displaying an overview of a mail.
 *
 * @author Jakob Korherr
 */
public class MailOverviewDto {

    private final String uuid;
    private final boolean isUnread;
    private final String from;
    private final String to;
    private final String subject;
    private final String dateTime;

    public MailOverviewDto(String uuid, boolean isUnread, String from, String to, String subject, String dateTime) {
        this.uuid = uuid;
        this.isUnread = isUnread;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.dateTime = dateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getDateTime() {
        return dateTime;
    }
}
