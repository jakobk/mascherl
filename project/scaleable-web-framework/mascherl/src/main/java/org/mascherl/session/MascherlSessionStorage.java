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
package org.mascherl.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mascherl.application.MascherlApplication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mascherl.MascherlConstants.MASCHERL_SESSION_REQUEST_ATTRIBUTE;

/**
 * Session storage implementation for {@link MascherlSession}.
 *
 * This class is responsible for restoring the user's {@link MascherlSession} from the current
 * {@link javax.servlet.http.HttpServletRequest}, and saving it into the current {@link javax.servlet.http.HttpServletResponse}.
 *
 * @see org.mascherl.session.MascherlSession
 *
 * @author Jakob Korherr
 */
public class MascherlSessionStorage {

    private static final Logger logger = Logger.getLogger(MascherlSessionStorage.class.getName());

    /**
     * Max size of the unencrypted data that can be stored in a cookie.
     *
     * 3039 characters result in an encrypted size of 4076.
     * 3040 characters would result in an encrypted size of 4096, which is too big for a cookie (4K including name and metadata).
     */
    public static final int MAX_DATA_SIZE = 3039;

    private static final String DEFAULT_APPLICATION_SECRET
            = "This is mascherl's secret session key for development. Changing this for production is a MUST.";

    private static final String SESSION_SECRET_CONFIG_KEY = "org.mascherl.session.secret";
    private static final String SESSION_TRANSFORMATION_CONFIG_KEY = "org.mascherl.session.transformation";
    private static final String COOKIE_NAME_CONFIG_KEY = "org.mascherl.session.cookie";

    private static final int EXPIRE_ON_BROWSER_CLOSE = -1;

    private final CryptoHelper cryptoHelper;
    private final ObjectMapper objectMapper;
    private final String cookieName;

    public MascherlSessionStorage() {
        Config config = ConfigFactory.load();

        cookieName = config.getString(COOKIE_NAME_CONFIG_KEY);

        String applicationSecret = config.getString(SESSION_SECRET_CONFIG_KEY);
        String transformation = config.getString(SESSION_TRANSFORMATION_CONFIG_KEY);
        cryptoHelper = new CryptoHelper(applicationSecret, transformation);

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    public void init(MascherlApplication mascherlApplication) {
        Config config = ConfigFactory.load();

        String applicationSecret = config.getString(SESSION_SECRET_CONFIG_KEY);
        if (!mascherlApplication.isDevelopmentMode() && Objects.equals(applicationSecret, DEFAULT_APPLICATION_SECRET)) {
            throw new RuntimeException("The application runs in production mode, but still uses the default " +
                    "application secret. This configuration is HIGHLY INSECURE, and thus initialization will be aborted.");
        }
    }

    public void saveSession(MascherlSession session, HttpServletResponse response) {
        if (!session.wasModified()) {
            return;  // no need to update an unmodified session
        }

        String data = session.serialize();
        if (data.length() > MAX_DATA_SIZE) {
            throw new IllegalStateException("Session data exceeds limit");
        }

        String encryptedValue = cryptoHelper.encryptAES(data);

        Cookie cookie = new Cookie(cookieName, encryptedValue);
        cookie.setMaxAge(EXPIRE_ON_BROWSER_CLOSE);
        response.addCookie(cookie);
    }

    public MascherlSession restoreSession(HttpServletRequest request) {
        MascherlSession mascherlSession = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies).filter(
                    (cookie) -> Objects.equals(cookieName, cookie.getName())).findAny();
            if (cookieOptional.isPresent()) {
                String encryptedValue = cookieOptional.get().getValue();
                try {
                    String data = cryptoHelper.decryptAES(encryptedValue);
                    mascherlSession = new MascherlSession(objectMapper, data);
                } catch (RuntimeException e) {
                    logger.log(Level.WARNING, "Session could not be restored. Will continue with empty session.", e);
                }
            }
        }
        if (mascherlSession == null) {
            mascherlSession = new MascherlSession(objectMapper);  // no session available, return a new one
        }

        request.setAttribute(MASCHERL_SESSION_REQUEST_ATTRIBUTE, mascherlSession);
        return mascherlSession;
    }

}
