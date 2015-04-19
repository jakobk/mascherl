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
package org.mascherl.validation.cxf;

import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationInInterceptor;
import org.apache.cxf.message.Message;
import org.mascherl.servlet.MascherlFilter;
import org.mascherl.validation.ValidationResult;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.List;

import static org.mascherl.MascherlConstants.MASCHERL_VALIDATION_RESULT_REQUEST_ATTRIBUTE;

/**
 * CXF specific request interceptor for bean validation of resource method parameters.
 *
 * This can currently not be done with plain JAX-RS api.
 *
 * Register via the following code in your spring.xml:
 * <pre>
 *     <jaxrs:inInterceptors>
 *         <bean class="org.mascherl.validation.cxf.CxfBeanValidationInInterceptor" />
 *     </jaxrs:inInterceptors>
 * </pre>
 *
 * @author Jakob Korherr
 */
public class CxfBeanValidationInInterceptor extends JAXRSBeanValidationInInterceptor {

    @Override
    protected void handleValidation(Message message, Object resourceInstance, Method method, List<Object> arguments) {
        ValidationResult validationResult;
        try {
            super.handleValidation(message, resourceInstance, method, arguments);
            validationResult = new ValidationResult();
        } catch (ConstraintViolationException e) {
            validationResult = new ValidationResult(e.getConstraintViolations());
        }
        MascherlFilter.getRequest().setAttribute(MASCHERL_VALIDATION_RESULT_REQUEST_ATTRIBUTE, validationResult);
    }
}
