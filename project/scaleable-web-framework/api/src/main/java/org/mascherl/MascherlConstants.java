package org.mascherl;

/**
 * Public constants used in Mascherl.
 *
 * @author Jakob Korherr
 */
public class MascherlConstants {

    public static class RequestParameters {
        public static final String M_CLIENT_URL = "m-client-url";
        public static final String M_CONTAINER = "m-container";
        public static final String M_FORM = "m-form";
        public static final String M_PAGE = "m-page";
        public static final String M_APP_VERSION = "m-app-version";
    }

    public static class ResponseHeaders {
        public static final String X_MASCHERL_TITLE = "X-Mascherl-Title";
        public static final String X_MASCHERL_PAGE = "X-Mascherl-Page";
        public static final String X_MASCHERL_CONTAINER = "X-Mascherl-Container";
        public static final String X_MASCHERL_URL = "X-Mascherl-Url";
        public static final String X_POWERED_BY = "X-Powered-By";
    }

    public static class RootScopeVariables {
        public static final String TITLE = "title";
        public static final String APPLICATION_VERSION = "applicationVersion";
        public static final String PAGE_ID = "pageId";
        public static final String URL = "url";
    }

    public static class Messages {
        public static String OUTDATED_VERSION_MSG = "The request failed, because you are using an outdated version of the " +
                "web application. The current page will be reloaded.";
    }

    public static final String MAIN_CONTAINER = "main";


}
