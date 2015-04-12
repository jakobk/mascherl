package org.mascherl.example.page;

import org.mascherl.example.domain.User;
import org.mascherl.session.MascherlSession;

/**
 * Utils for page classes.
 *
 * @author Jakob Korherr
 */
public class PageUtils {

    public static User getCurrentUser() {
        MascherlSession session = MascherlSession.getInstance();
        return session.get("user", User.class);
    }

}
