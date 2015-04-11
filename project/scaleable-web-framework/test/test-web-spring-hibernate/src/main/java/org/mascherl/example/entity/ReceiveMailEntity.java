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
public class ReceiveMailEntity extends MailEntity {

    @ManyToOne
    @JoinColumn(name = "receive_user")
    private UserEntity receiveUser;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @Column(name = "receive_datetime")
    private ZonedDateTime receiveDateTime;

    public ReceiveMailEntity() {}

    public UserEntity getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(UserEntity receiveUser) {
        this.receiveUser = receiveUser;
    }

    public ZonedDateTime getReceiveDateTime() {
        return receiveDateTime;
    }

    public void setReceiveDateTime(ZonedDateTime receiveDateTime) {
        this.receiveDateTime = receiveDateTime;
    }
}
