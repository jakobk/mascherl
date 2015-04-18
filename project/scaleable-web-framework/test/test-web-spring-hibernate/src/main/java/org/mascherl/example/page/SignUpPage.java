/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.example.page;

import org.mascherl.example.domain.SignUpRequest;
import org.mascherl.example.page.data.SelectOption;
import org.mascherl.example.page.data.SignUpStep1Bean;
import org.mascherl.example.page.data.SignUpStep2Bean;
import org.mascherl.example.service.SignUpService;
import org.mascherl.page.Mascherl;
import org.mascherl.page.MascherlAction;
import org.mascherl.page.MascherlPage;
import org.mascherl.page.Model;
import org.mascherl.session.MascherlSession;
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
import java.util.Arrays;
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

    @Inject
    private MascherlSession session;

    @Inject
    private IndexPage indexPage;

    @GET
    @Path("/signup")
    public MascherlPage signUp() {
        SignUpStep1Bean bean = session.get("signUpStep1", SignUpStep1Bean.class);

        return Mascherl.page("/templates/root/signupStep1.html")
                .pageTitle("SignUp - WebMail powered by Mascherl")
                .container("dialogContent", (model) -> {
                    model.put("bean", bean == null ? new SignUpStep1Bean() : bean);
                    model.put("countries", convertToSelectOptions(signUpService.getCountries(), null));
                })
                .container("stateContainer", (model) -> model.put("states", convertToSelectOptions(signUpService.getStates("Austria"), null)));
    }

    @GET
    @Path("/signup/2")
    public MascherlPage signUpStep2() {
        SignUpStep1Bean signUpStep1 = session.get("signUpStep1", SignUpStep1Bean.class);
        if (signUpStep1 != null) {
            return Mascherl.page("/templates/root/signupStep2.html")
                    .pageTitle("SignUp - WebMail powered by Mascherl")
                    .container("dialogContent", (model) -> {
                        model.put("bean", new SignUpStep2Bean());
                    });
        } else {
            return signUp().replaceUrl("/signup");
        }
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
    public MascherlAction signUpStep1(@Valid @BeanParam SignUpStep1Bean bean) {
        if (validationResult.isValid()) {
            session.put("signUpStep1", bean);
            return Mascherl
                    .navigate("/signup/2")
                    .renderContainer("dialogContent")
                    .withPageDef(signUpStep2());
        } else {
            return Mascherl
                    .stay()
                    .renderContainer("dialogContent")
                    .withPageDef(signUp()
                            .container("dialogContent", (model) -> {
                                model.put("bean", bean);
                                model.put("countries", convertToSelectOptions(signUpService.getCountries(), bean.getCountry()));
                                addValidationErrors(model, "firstName", "lastName", "dateOfBirth", "country", "state");
                            })
                            .container("stateContainer", (model) -> model.put("states", convertToSelectOptions(signUpService.getStates(bean.getCountry()), bean.getState())))
                            .container("dialogMessages", (model) -> model.put("errorMsg", "Invalid input.")));
        }
    }

    @POST
    @Path("/signup/step2")
    public MascherlAction signUpStep2(@Valid @BeanParam SignUpStep2Bean bean) {
        SignUpStep1Bean signUpStep1 = session.get("signUpStep1", SignUpStep1Bean.class);
        if (signUpStep1 == null) {
            return Mascherl
                    .navigate("/signup")
                    .renderContainer("dialogContent")
                    .withPageDef(signUp().container("dialogMessages", (model) -> model.put("errorMsg", "You need to complete step 1 first.")));
        }

        boolean serviceCallError = false;
        if (validationResult.isValid()) {
            try {
                signUpService.signUp(new SignUpRequest(
                        signUpStep1.getFirstName(),
                        signUpStep1.getLastName(),
                        signUpStep1.getDateOfBirth(),
                        signUpStep1.getCountry(),
                        signUpStep1.getState(),
                        bean.getEmail(),
                        bean.getPassword()
                ));
            } catch (RuntimeException e) {
                serviceCallError = true;
            }

            if (!serviceCallError) {
                session.remove("signUpStep1");
                return Mascherl
                        .navigate("/login")
                        .renderAll()
                        .withPageDef(
                                indexPage.login()
                                        .container("messages", (model) -> model.put("successMsg", "Successfully signed up. You can now log in.")));
            }
        }

        boolean emailAddressExists = serviceCallError;
        return Mascherl
                .stay()
                .renderContainer("dialogContent")
                .withPageDef(signUpStep2()
                        .container("dialogContent", (model) -> {
                            model.put("bean", bean);
                            addValidationErrors(model, "email", "password", "passwordRepeat");
                            if (hasValidationError("passwordsMatching")) {
                                model.put("passwordError", true);
                                model.put("passwordRepeatError", true);
                            }

                            if (emailAddressExists) {
                                model.put("emailError", true);
                            }
                        })
                        .container("dialogMessages", (model) -> {
                            if (emailAddressExists) {
                                model.put("errorMsg", "User with this email address already exists.");
                            } else {
                                model.put("errorMsg", "Invalid input.");
                            }
                        }));
    }

    private void addValidationErrors(Model model, String... fields) {
        Arrays.stream(fields)
                .filter(this::hasValidationError)
                .forEach(field -> model.put(field + "Error", true));
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
