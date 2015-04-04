package org.mascherl.session;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class MascherlSessionHolder {

    public static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    public static MascherlSession getSession() {
        return (MascherlSession) requestThreadLocal.get().getAttribute("MASCHERL_SESSION");
    }

}
