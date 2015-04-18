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
package org.mascherl.page;

import org.mascherl.render.ContainerMeta;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.mascherl.page.Mascherl.getContainerMeta;

/**
 * Mascherl page, which allows to build the container models lazily at a later point in time.
 *
 * @author Jakob Korherr
 */
public class LazyMascherlPage extends MascherlPage {

    private final Map<String, List<ModelCalculator>> containerModelCalculators = new HashMap<>();
    private boolean isBuild = false;

    LazyMascherlPage(String template) {
        super(template);
    }

    @Override
    public MascherlPage container(String containerName, ModelCalculator modelCalculator) {
        if (isBuild) {
            return super.container(containerName, modelCalculator);
        } else {
            List<ModelCalculator> containerCalculatorList = containerModelCalculators.get(containerName);
            if (containerCalculatorList == null) {
                containerCalculatorList = new LinkedList<>();
                containerModelCalculators.put(containerName, containerCalculatorList);
            }
            containerCalculatorList.add(modelCalculator);
            return this;
        }
    }

    @Override
    public Map<String, Model> getContainerModels() {
        if (isBuild) {
            return super.getContainerModels();
        } else {
            throw new IllegalStateException("You need to build() a normal " + MascherlPage.class.getSimpleName() + " first");
        }
    }

    public void build(HttpServletRequest request) {
        ContainerMeta requestContainerMeta = getContainerMeta(getTemplate(), request);
        findContainersToEvaluate(requestContainerMeta);

        for (Map.Entry<String, List<ModelCalculator>> modelCalculatorsEntry : containerModelCalculators.entrySet()) {
            for (ModelCalculator modelCalculator : modelCalculatorsEntry.getValue()) {
                super.container(modelCalculatorsEntry.getKey(), modelCalculator);
            }
        }
        isBuild = true;
    }


}
