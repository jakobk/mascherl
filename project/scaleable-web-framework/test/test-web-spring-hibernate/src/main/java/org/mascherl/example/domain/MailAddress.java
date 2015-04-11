package org.mascherl.example.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Embeddable
public class MailAddress {

    @Column(name = "address", length = 255)
    private String address;

    protected MailAddress() {
    }

    public MailAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MailAddress)) return false;

        MailAddress that = (MailAddress) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MailAddress{" +
                "address='" + address + '\'' +
                '}';
    }
}
