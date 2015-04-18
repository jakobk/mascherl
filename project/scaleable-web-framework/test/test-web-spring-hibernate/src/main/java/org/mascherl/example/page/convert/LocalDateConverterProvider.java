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
