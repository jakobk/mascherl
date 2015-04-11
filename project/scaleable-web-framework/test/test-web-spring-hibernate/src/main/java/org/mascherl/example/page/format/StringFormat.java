package org.mascherl.example.page.format;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class StringFormat {

    public static String truncate(String s, int maxLength) {
        if (s.length() > maxLength) {
            return s.substring(0, maxLength - 3) + "...";
        }
        return s;
    }

}
