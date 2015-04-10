package org.mascherl.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Column(name = "login_alias", length = 255)
    private String loginAlias;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    public String getLoginAlias() {
        return loginAlias;
    }

    public void setLoginAlias(String loginAlias) {
        this.loginAlias = loginAlias;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
