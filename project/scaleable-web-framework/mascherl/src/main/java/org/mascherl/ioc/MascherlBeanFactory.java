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
