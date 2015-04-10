package org.mascherl.example;

import javax.ws.rs.FormParam;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class OverviewForm {

    @FormParam("firstname")
    private String firstname;

    @FormParam("lastname")
    private String lastname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
