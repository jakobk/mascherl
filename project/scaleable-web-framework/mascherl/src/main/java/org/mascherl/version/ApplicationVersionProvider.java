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
package org.mascherl.version;

/**
 * Provider for the current version of the application using Mascherl.
 *
 * An own implementation of this class can be registered via the ServiceLoader mechanism (META-INF/services),
 * and will then be picked up by the initializer of Mascherl.
 *
 * @author Jakob Korherr
 */
public interface ApplicationVersionProvider {

    public ApplicationVersion getApplicationVersion();

}
