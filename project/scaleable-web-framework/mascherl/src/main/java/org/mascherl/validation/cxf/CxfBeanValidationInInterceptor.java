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
