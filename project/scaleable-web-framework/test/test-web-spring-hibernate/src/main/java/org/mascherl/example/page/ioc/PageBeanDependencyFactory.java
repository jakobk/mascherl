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
package org.mascherl.example.page.ioc;

import org.mascherl.example.domain.User;
import org.mascherl.session.MascherlSession;
import org.springframework.stereotype.Component;

/**
 * Utils for page classes.
 *
 * @author Jakob Korherr
 */
@Component
public class PageBeanDependencyFactory {

    public User getCurrentUser() {
        return MascherlSession.getInstance().get("user", User.class);
    }

}
