package org.mascherl.example.page.data;

import org.mascherl.example.validation.Email;

import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

/**
 * Bean for performing login.
 *
 * @author Jakob Korherr
 */
public class LoginBean {

    @FormParam("email")
    @Size(min = 1)
    @Email
    private String email;

    @FormParam("password")
    @Size(min = 1)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
