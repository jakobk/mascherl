package org.mascherl.example.page.data;

import javax.ws.rs.FormParam;

/**
 * Bean containing the form data of the compose mail form.
 *
 * @author Jakob Korherr
 */
public class ComposeMailBean {

    @FormParam("to")
    private String to;

    @FormParam("cc")
    private String cc;

    @FormParam("bcc")
    private String bcc;

    @FormParam("subject")
    private String subject;

    @FormParam("messageText")
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
