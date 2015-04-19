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
package org.mascherl.example.service;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.SignUpRequest;
import org.mascherl.example.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mascherl.example.service.LoginService.sha256;

/**
 * Service for sign up of new users.
 *
 * @author Jakob Korherr
 */
@Service
public class SignUpService {

    private static final Map<String, List<String>> countryStateIndex = new HashMap<>();
    static {
        countryStateIndex.put("Austria", Arrays.asList(
                "Vienna",
                "Lower Austria",
                "Upper Austria",
                "Salzburg",
                "Styria",
                "Tyrol",
                "Vorarlberg",
                "Burgenland",
                "Carinthia"
        ));
        countryStateIndex.put("Germany", Arrays.asList(
                "Baden-Württemberg",
                "Bavaria",
                "Berlin",
                "Brandenburg",
                "Bremen",
                "Hamburg",
                "Hesse",
                "Lower Saxony",
                "Mecklenburg-Vorpommern",
                "North Rhine-Westphalia",
                "Rhineland-Palatinate",
                "Saarland",
                "Saxony",
                "Saxony-Anhalt",
                "Schleswig-Holstein",
                "Thuringia"
        ));
    }

    @PersistenceContext
    private EntityManager em;

    @Inject
    private SendMailService sendMailService;

    public List<String> getCountries() {
        return Arrays.asList("Austria", "Germany", "other");
    }

    public List<String> getStates(String country) {
        List<String> states = countryStateIndex.get(country);
        if (states == null) {
            states = Arrays.asList("-");
        }
        return states;
    }

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        boolean userExists = !em.createQuery(
                "select u.uuid from UserEntity u where u.email = :email")
                .setParameter("email", signUpRequest.getEmail())
                .getResultList().isEmpty();
        if (userExists) {
            throw new IllegalStateException("User with given email already exists");
        }

        UserEntity entity = new UserEntity();
        entity.setFirstName(signUpRequest.getFirstName());
        entity.setLastName(signUpRequest.getLastName());
        entity.setEmail(signUpRequest.getEmail());
        entity.setPasswordHash(sha256(signUpRequest.getPassword()));
        entity.setDateOfBirth(signUpRequest.getDateOfBirth());
        entity.setCountry(signUpRequest.getCountry());
        entity.setState(signUpRequest.getState());
        em.persist(entity);
        em.flush();

        sendMailService.sendMailFromSystem(new Mail(
                new MailAddress("webmail@mascherl.org"),
                Collections.singleton(new MailAddress(signUpRequest.getEmail())),
                null,
                null,
                "Welcome to Mascherl WebMail",
                "Hello " + signUpRequest.getFirstName() + " " + signUpRequest.getLastName() + "!\n" +
                        "\n" +
                        "Your e-mail address is: " + signUpRequest.getEmail() + "\n" +
                        "\n" +
                        "We wish you a lot of fun with Mascherl WebMail.\n" +
                        "\n" +
                        "Cheers,\n" +
                        "Your Mascherl WebMail team.\n"
        ));
    }



}
