package org.mascherl.example.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.ZonedDateTime;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Entity
public class SendMailEntity extends MailEntity {

    @ManyToOne
    @JoinColumn(name = "send_user")
    private UserEntity sendUser;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @Column(name = "send_datetime")
    private ZonedDateTime sendDateTime;

    public SendMailEntity() {}

    public UserEntity getSendUser() {
        return sendUser;
    }

    public void setSendUser(UserEntity sendUser) {
        this.sendUser = sendUser;
    }

    public ZonedDateTime getSendDateTime() {
        return sendDateTime;
    }

    public void setSendDateTime(ZonedDateTime sendDateTime) {
        this.sendDateTime = sendDateTime;
    }
}
