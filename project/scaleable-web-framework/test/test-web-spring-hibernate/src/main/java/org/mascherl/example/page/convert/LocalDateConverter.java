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
