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

import org.mascherl.example.domain.User;
import org.mascherl.example.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * Service for login and sign up.
 *
 * @author Jakob Korherr
 */
@Service
public class LoginService {

    private static final String SHA_256 = "SHA-256";

    @PersistenceContext
    private EntityManager em;

    public User login(String email, String password) {
        List<User> resultList = em.createQuery(
                "select new " + User.class.getName() + " (" +
                        "u.uuid, " +
                        "u.firstName, " +
                        "u.lastName, " +
                        "u.email " +
                        ") " +
                        "from UserEntity u " +
                        "where u.email = :email " +
                        "and u.passwordHash = :passwordHash", User.class)
                .setParameter("email", email)
                .setParameter("passwordHash", sha256(password))
                .getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    @Transactional
    public void createNewUser(User user, String password) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(sha256(password));
        em.persist(entity);
        em.flush();
    }

    public static String sha256(String value) {
        MessageDigest messageDigest = createMessageDigest();
        messageDigest.update(value.getBytes(StandardCharsets.UTF_8));
        byte[] digest = messageDigest.digest();
        byte[] base64Digest = Base64.getEncoder().encode(digest);
        return new String(base64Digest, StandardCharsets.UTF_8);
    }

    private static MessageDigest createMessageDigest() {
        try {
            return MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
