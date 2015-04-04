package org.mascherl.session;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mascherl.application.MascherlApplication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.mascherl.MascherlConstants.MASCHERL_SESSION_COOKIE;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MascherlSessionStorage {

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

    private static final int EXPIRE_ON_BROWSER_CLOSE = -1;

    private CryptoHelper cryptoHelper;

    public MascherlSessionStorage() {
    }

    public void init(MascherlApplication mascherlApplication) {
        Config config = ConfigFactory.load();

        String applicationSecret = config.getString(SESSION_SECRET_CONFIG_KEY);
        if (!mascherlApplication.isDevelopmentMode() && Objects.equals(applicationSecret, DEFAULT_APPLICATION_SECRET)) {
            throw new RuntimeException("The application runs in production mode, but still uses the default " +
                    "application secret. This configuration is HIGHLY INSECURE, and thus initialization will be aborted.");
        }

        cryptoHelper = new CryptoHelper(applicationSecret, config.getString(SESSION_TRANSFORMATION_CONFIG_KEY));
    }

    public void saveSession(MascherlSession session, HttpServletResponse response) {
        MascherlSessionHolder.requestThreadLocal.remove();

        if (!session.wasModified()) {
            return;  // no need to update an unmodified session
        }

        String data = session.serialize();
        if (data.length() > MAX_DATA_SIZE) {
            throw new IllegalStateException("Session data exceeds limit");
        }

        String encryptedValue = cryptoHelper.encryptAES(data);

        Cookie cookie = new Cookie(MASCHERL_SESSION_COOKIE, encryptedValue);
        cookie.setMaxAge(EXPIRE_ON_BROWSER_CLOSE);
        response.addCookie(cookie);
    }

    public MascherlSession restoreSession(HttpServletRequest request) {
        MascherlSessionHolder.requestThreadLocal.set(request);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies).filter(
                    (cookie) -> Objects.equals(MASCHERL_SESSION_COOKIE, cookie.getName())).findAny();
            if (cookieOptional.isPresent()) {
                String encryptedValue = cookieOptional.get().getValue();
                String data = cryptoHelper.decryptAES(encryptedValue);
                return new MascherlSession(data);
            }
        }
        return new MascherlSession();  // no session available, return a new one
    }

}
