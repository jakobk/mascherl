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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

/**
 * Bean containing the form data of the compose mail form.
 *
 * @author Jakob Korherr
 */
public class ComposeMailBean {

    /**
     * Validation group for mail sending.
     */
    public static interface Send {}

    // TODO custom validators for to, cc, bcc

    @FormParam("to")
    @NotNull(groups = Send.class)
    @Size(min = 1, groups = Send.class)
    private String to;

    @FormParam("cc")
    private String cc;

    @FormParam("bcc")
    private String bcc;

    @FormParam("subject")
    @NotNull(groups = Send.class)
    @Size(min = 1, groups = Send.class)
    private String subject;

    @FormParam("messageText")
    @NotNull(groups = Send.class)
    private String messageText;

    public ComposeMailBean() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
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
