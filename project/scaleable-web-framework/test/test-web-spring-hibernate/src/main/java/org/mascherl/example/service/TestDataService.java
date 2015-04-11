package org.mascherl.example.service;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collections;

/**
 * TODO
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
        loginService.createNewUser(new User("Steffi", "Pollmann", "SPollmann@gmx.at"), "pwd");

        User userJakobKorherr = loginService.login("jakob.korherr@gmail.com", "pwd");
        User userSteffiPollmann = loginService.login("SPollmann@gmx.at", "pwd");

        sendMailService.sendMail(
                new Mail(
                        new MailAddress("jakob.korherr@gmail.com"),
                        Collections.singleton(new MailAddress("SPollmann@gmx.at")),
                        Collections.singleton(new MailAddress("some.other.address@asdf.com")),
                        null,
                        "Test subject",
                        "This is a test mail!"),
                userJakobKorherr);
        sendMailService.sendMail(
                new Mail(
                        new MailAddress("SPollmann@gmx.at"),
                        Collections.singleton(new MailAddress("jakob.korherr@gmail.com")),
                        null,
                        null,
                        "RE: Test subject",
                        "This is a test mail!"),
                userSteffiPollmann);
    }

}
