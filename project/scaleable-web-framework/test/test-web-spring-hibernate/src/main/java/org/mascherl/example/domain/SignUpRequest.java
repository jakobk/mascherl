package org.mascherl.example.domain;

import java.time.LocalDate;

/**
 * Domain object for signing up a new user.
 *
 * @author Jakob Korherr
 */
public class SignUpRequest {

    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final String country;
    private final String state;
    private final String email;
    private final String password;

    public SignUpRequest(String firstName, String lastName, LocalDate dateOfBirth, String country, String state, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
        this.state = state;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
