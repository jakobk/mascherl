package org.mascherl.example.page.convert;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * Converter provider for {@link LocalDate}.
 *
 * @author Jakob Korherr
 */
@Provider
public class LocalDateConverterProvider implements ParamConverterProvider{

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (LocalDate.class.isAssignableFrom(rawType)) {
            @SuppressWarnings("unchecked")
            ParamConverter<T> tParamConverter = (ParamConverter<T>) new LocalDateConverter();
            return tParamConverter;
        }
        return null;
    }

}
