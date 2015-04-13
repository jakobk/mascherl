package org.mascherl.ioc;

import org.mascherl.session.MascherlSession;
import org.mascherl.validation.ValidationResult;

/**
 * Factory class for beans, which can be injected via Spring, CDI, Guice, or any other compatible IoC framework.
 *
 * @author Jakob Korherr
 */
public class MascherlBeanFactory {

    public MascherlSession getSession() {
        return MascherlSession.getInstance();
    }

    public ValidationResult getValidationResult() {
        return ValidationResult.getInstance();
    }

}
