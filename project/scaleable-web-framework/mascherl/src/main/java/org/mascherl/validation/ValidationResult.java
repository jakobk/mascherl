package org.mascherl.validation;

import org.mascherl.servlet.MascherlFilter;

import javax.validation.ConstraintViolation;
import java.util.Collections;
import java.util.Set;

import static org.mascherl.MascherlConstants.MASCHERL_VALIDATION_RESULT_REQUEST_ATTRIBUTE;

/**
 * Result of a bean validation for current request.
 *
 * @author Jakob Korherr
 */
public class ValidationResult {

    public static ValidationResult getInstance() {
        return (ValidationResult) MascherlFilter.getRequest().getAttribute(MASCHERL_VALIDATION_RESULT_REQUEST_ATTRIBUTE);
    }

    private final Set<ConstraintViolation<?>> constraintViolations;

    public ValidationResult() {
        this(null);
    }

    public ValidationResult(Set<ConstraintViolation<?>> constraintViolations) {
        if (constraintViolations == null) {
            constraintViolations = Collections.emptySet();
        }
        this.constraintViolations = constraintViolations;
    }

    public boolean isValid() {
        return constraintViolations.isEmpty();
    }

    public boolean isFailed() {
        return !isValid();
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }

}
