package org.mascherl.spring;

import org.mascherl.session.MascherlSession;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MascherlSessionFactory {

    public MascherlSession getSession() {
        return MascherlSession.getInstance();
    }

}
