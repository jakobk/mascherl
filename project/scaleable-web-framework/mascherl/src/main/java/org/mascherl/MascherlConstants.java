package org.mascherl;

/**
 * Public constants used in Mascherl.
 *
 * @author Jakob Korherr
 */
public class MascherlConstants {

    /**
     * @see javax.ws.rs.Priorities
     */
    public static final int FILTER_PRIORITY = 500;

    public static class RequestParameters {
        public static final String M_CLIENT_URL = "m-client-url";
        public static final String M_CONTAINER = "m-container";
        public static final String M_PAGE = "m-page";
        public static final String M_APP_VERSION = "m-app-version";
    }

    public static class ResponseHeaders {
        public static final String X_MASCHERL_TITLE = "X-Mascherl-Title";
        public static final String X_MASCHERL_PAGE = "X-Mascherl-Page";
        public static final String X_MASCHERL_CONTAINER = "X-Mascherl-Container";
        public static final String X_MASCHERL_URL = "X-Mascherl-Url";
        public static final String X_MASCHERL_REPLACE_URL = "X-Mascherl-Replace-Url";
        public static final String X_POWERED_BY = "X-Powered-By";
    }

    public static class RequestHeaders {
        public static final String X_MASCHERL_CONTAINER = "X-Mascherl-Container";
        public static final String X_MASCHERL_APP_VERSION = "X-Mascherl-App-Version";
    }

    public static class RootScopeVariables {
        public static final String TITLE = "title";
        public static final String APPLICATION_VERSION = "applicationVersion";
        public static final String PAGE_GROUP = "pageGroup";
        public static final String URL_FUNCTION = "url";
    }

    public static final String MAIN_CONTAINER = "main";

    public static final String MASCHERL_SESSION_REQUEST_ATTRIBUTE = "MASCHERL_SESSION";
    public static final String MASCHERL_VALIDATION_RESULT_REQUEST_ATTRIBUTE = "MASCHERL_VALIDATION_RESULT";
    public static final String MASCHERL_APPLICATION_CONTEXT_ATTRIBUTE = "MASCHERL_APPLICATION";



}
