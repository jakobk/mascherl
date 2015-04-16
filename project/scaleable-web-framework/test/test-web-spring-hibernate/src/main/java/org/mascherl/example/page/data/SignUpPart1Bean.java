package org.mascherl.example.page.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.time.LocalDate;

/**
 * Bean for sign up form, part 1.
 *
 * @author Jakob Korherr
 */
public class SignUpPart1Bean {

    @FormParam("firstName")
    @Size(min = 1)
    @NotNull
    private String firstName;

    @FormParam("lastName")
    @Size(min = 1)
    @NotNull
    private String lastName;

    @FormParam("dateOfBirth")
    @Past
    @NotNull
    private LocalDate dateOfBirth;

    @FormParam("country")
    @Size(min = 2)
    @NotNull
    private String country;

    @FormParam("state")
    @Size(min = 1)
    @NotNull
    private String state;

}
