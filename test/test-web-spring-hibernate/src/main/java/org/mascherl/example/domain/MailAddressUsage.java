package org.mascherl.example.domain;

import java.time.ZonedDateTime;

/**
 * An usage of a {@link MailAddress} at a given point in time.
 *
 * @author Jakob Korherr
 */
public class MailAddressUsage {

    private final MailAddress mailAddress;
    private final ZonedDateTime dateTime;

    public MailAddressUsage(MailAddress mailAddress, ZonedDateTime dateTime) {
        this.mailAddress = mailAddress;
        this.dateTime = dateTime;
    }

    public MailAddress getMailAddress() {
        return mailAddress;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailAddressUsage that = (MailAddressUsage) o;

        if (mailAddress != null ? !mailAddress.equals(that.mailAddress) : that.mailAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mailAddress != null ? mailAddress.hashCode() : 0;
    }
}
