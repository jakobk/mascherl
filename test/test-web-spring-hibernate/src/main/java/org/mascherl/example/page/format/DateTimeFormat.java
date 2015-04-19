/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.example.page.format;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date time format utils.
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
