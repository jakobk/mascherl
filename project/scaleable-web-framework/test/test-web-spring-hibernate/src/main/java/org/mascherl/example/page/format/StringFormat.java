package org.mascherl.example.page.format;

import java.util.List;

/**
 * String format utils.
 *
 * @author Jakob Korherr
 */
public class StringFormat {

    public static String truncate(String s, int maxLength) {
        if (s != null && s.length() > maxLength) {
            return s.substring(0, maxLength - 3) + "...";
        }
        return s;
    }

    public static String pluralize(String singular, List<?> dataList) {
        return singular + (dataList.size() == 1 ? "" : "s");
    }

}
