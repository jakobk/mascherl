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
package org.mascherl.example.page.convert;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Param converter for {@link LocalDate}, which allows using LocalDate in @FormParam, @QueryParam, etc.
 *
 * @author Jakob Korherr
 */
public class LocalDateConverter implements ParamConverter<LocalDate> {

    private static final String PATTERN = "yyyy-MM-dd";

    @Override
    public LocalDate fromString(String value) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(PATTERN));
    }

    @Override
    public String toString(LocalDate value) {
        return value.format(DateTimeFormatter.ofPattern(PATTERN));
    }

}
