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
