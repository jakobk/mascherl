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
import org.mascherl.example.domain.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collections;

/**
 * Inserts some test data upon server startup.
 *
 * @author Jakob Korherr
 */
@Service
public class TestDataService {

    @Inject
    private LoginService loginService;

    @Inject
    private SendMailService sendMailService;

    @PostConstruct
    public void init() {
        loginService.createNewUser(new User("Jakob", "Korherr", "jakob.korherr@gmail.com"), "pwd");
        loginService.createNewUser(new User("Steffi", "Pollmann", "steffi.pollmann@gmail.com"), "pwd");

        User userJakobKorherr = loginService.login("jakob.korherr@gmail.com", "pwd");
        User userSteffiPollmann = loginService.login("steffi.pollmann@gmail.com", "pwd");

        sendMailService.sendMail(
                new Mail(
                        new MailAddress("jakob.korherr@gmail.com"),
                        Collections.singleton(new MailAddress("steffi.pollmann@gmail.com")),
                        Collections.singleton(new MailAddress("some.other.address@asdf.com")),
                        null,
                        "Test subject",
                        "This is a test mail!"),
                userJakobKorherr);
        sendMailService.sendMail(
                new Mail(
                        new MailAddress("jakob.korherr@gmail.com"),
                        Collections.singleton(new MailAddress("steffi.pollmann@gmail.com")),
                        null,
                        null,
                        "Test subject 2",
                        "This is another test mail!"),
                userJakobKorherr);
        sendMailService.sendMail(
                new Mail(
                        new MailAddress("steffi.pollmann@gmail.com"),
                        Collections.singleton(new MailAddress("jakob.korherr@gmail.com")),
                        null,
                        null,
                        "RE: Test subject",
                        "This is a test mail!"),
                userSteffiPollmann);
    }

}
