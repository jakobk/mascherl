package org.mascherl.example.page.ioc;

import org.mascherl.example.domain.User;
import org.mascherl.session.MascherlSession;
import org.springframework.stereotype.Component;

/**
 * Utils for page classes.
 *
 * @author Jakob Korherr
 */
@Component
public class PageBeanDependencyFactory {

    public User getCurrentUser() {
        return MascherlSession.getInstance().get("user", User.class);
    }

}
