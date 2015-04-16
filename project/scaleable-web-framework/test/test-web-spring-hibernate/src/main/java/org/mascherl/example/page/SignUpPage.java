package org.mascherl.example.page;

import org.mascherl.example.page.data.SelectOption;
import org.mascherl.example.page.data.SignUpPart1Bean;
import org.mascherl.example.service.SignUpService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.mascherl.validation.ValidationResult;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static javax.validation.Path.Node;

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
                .container("dialogContent", (model) -> {
                    model.put("bean", new SignUpPart1Bean());
                    model.put("countries", convertToSelectOptions(signUpService.getCountries(), null));
                })
                .container("stateContainer", (model) -> model.put("states", convertToSelectOptions(signUpService.getStates("Austria"), null)));
    }

    @POST
    @Path("/signup/selectCountry")
    public MascherlAction selectCountry(@FormParam("country") String country) {
        return Mascherl
                .stay()
                .renderContainer("stateContainer")
                .withPageDef(signUp()
                        .container("stateContainer", (model) -> model.put("states", convertToSelectOptions(signUpService.getStates(country), null))));
    }

    @POST
    @Path("/signup/step1")
    public MascherlAction signupStep1(@Valid @BeanParam SignUpPart1Bean bean) {
        if (validationResult.isValid()) {
            return Mascherl
                    .navigate("/login")
                    .redirect(); // TODO next step
        } else {
            return Mascherl
                    .stay()
                    .renderContainer("dialogContent")
                    .withPageDef(signUp()
                            .container("dialogContent", (model) -> {
                                model.put("bean", bean);
                                model.put("countries", convertToSelectOptions(signUpService.getCountries(), bean.getCountry()));

                                if (hasValidationError("firstName")) {
                                    model.put("firstNameError", true);
                                }
                                if (hasValidationError("lastName")) {
                                    model.put("lastNameError", true);
                                }
                                if (hasValidationError("dateOfBirth")) {
                                    model.put("dateOfBirthError", true);
                                }
                                if (hasValidationError("country")) {
                                    model.put("countryError", true);
                                }
                                if (hasValidationError("state")) {
                                    model.put("stateError", true);
                                }
                            })
                            .container("stateContainer", (model) -> model.put("states", convertToSelectOptions(signUpService.getStates(bean.getCountry()), bean.getState())))
                            .container("dialogMessages", (model) -> model.put("errorMsg", "Invalid input.")));
        }
    }

    private boolean hasValidationError(String field) {
        return validationResult.getConstraintViolations().stream()
                .map(ConstraintViolation::getPropertyPath)
                .map((path) -> {
                    Node last = null;
                    for (Node node : path) {
                        last = node;
                    }
                    return last == null ? null : last.getName();
                })
                .anyMatch((property) -> Objects.equals(property, field));
    }

    private List<SelectOption> convertToSelectOptions(List<String> options, String selected) {
        return options.stream().map((option) -> new SelectOption(option, option, Objects.equals(option, selected))).collect(Collectors.toList());
    }

}
