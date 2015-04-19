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
package org.mascherl.example.balancer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selects the configured servers per round robin strategy.
 *
 * @author Jakob Korherr
 */
public class RoundRobinServerSelector {

    private final AtomicInteger index = new AtomicInteger(0);
    private final List<InetSocketAddress> servers = new ArrayList<>();

    public RoundRobinServerSelector() {
        servers.add(new InetSocketAddress("localhost", 8080));
        servers.add(new InetSocketAddress("localhost", 8081));
        servers.add(new InetSocketAddress("localhost", 8082));
    }

    public InetSocketAddress selectServer() {
        int serverIndex = index.getAndUpdate((i) -> {
            i++;
            if (i >= servers.size()) {
                i = 0;
            }
            return i;
        });
        return servers.get(serverIndex);
    }

}
