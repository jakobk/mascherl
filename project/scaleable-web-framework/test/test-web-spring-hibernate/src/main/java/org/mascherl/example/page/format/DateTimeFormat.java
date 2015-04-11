package org.mascherl.example.page.format;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class DateTimeFormat {

    public static String formatDateTime(ZonedDateTime dateTime) {
        ZonedDateTime now = ZonedDateTime.now(dateTime.getZone());
        long dayDifference = dateTime.truncatedTo(ChronoUnit.DAYS).until(now.truncatedTo(ChronoUnit.DAYS), ChronoUnit.DAYS);
        if (dayDifference == 0) {
            return "Today " + dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else if (dayDifference == 1) {
            return "Yesterday " + dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        }
    }

}
