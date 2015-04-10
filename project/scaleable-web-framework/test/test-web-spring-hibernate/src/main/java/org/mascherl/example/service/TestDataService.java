package org.mascherl.example.service;

import org.mascherl.example.domain.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Service
public class TestDataService {

    @Inject
    private LoginService loginService;

    @PostConstruct
    public void init() {
        loginService.createNewUser(new User("Jakob", "Korherr", "jakob.korherr@gmail.com"), "pwd");

    }

}
