package org.mascherl.example.page;

import org.mascherl.example.page.data.SignUpPart1Bean;
import org.mascherl.example.service.SignUpService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.mascherl.validation.ValidationResult;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static org.mascherl.example.page.PageUtils.getValidationErrorMessages;

/**
 * Page class for the sign-up dialog.
 *
 * @author Jakob Korherr
 */
@Component
public class SignUpPage {

    @Inject
    private SignUpService signUpService;

    @Inject
    private ValidationResult validationResult;

    @GET
    @Path("/signup")
    public MascherlPage signUp() {
        return Mascherl.page("/templates/root/signupStep1.html")
                .pageTitle("SignUp - WebMail powered by Mascherl")
                .container("stateContainer", (model) -> model.put("states", signUpService.getStates("AT")));
    }

    @POST
    @Path("/signup/selectCountry")
    public MascherlAction selectCountry(@FormParam("country") String country) {
        return Mascherl
                .stay()
                .renderContainer("stateContainer")
                .withPageDef(signUp().container("stateContainer", (model) -> model.put("states", signUpService.getStates(country))));
    }

    @POST
    @Path("/signup/part1")
    public MascherlAction signupPart1(@Valid @BeanParam SignUpPart1Bean bean) {
        if (validationResult.isValid()) {
            return Mascherl
                    .navigate("/login")
                    .redirect(); // TODO next step
        } else {
            return Mascherl
                    .stay()
                    .renderContainer("dialogMessages")
                    .withPageDef(signUp()
                            .container("dialogMessages", (model) -> model.put("errorMsg", getValidationErrorMessages(validationResult))));
        }
    }

}
