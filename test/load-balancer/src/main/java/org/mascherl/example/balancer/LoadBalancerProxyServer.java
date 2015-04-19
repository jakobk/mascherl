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

import org.mascherl.example.proxy.TcpProxy;
import org.mascherl.example.proxy.TcpProxyConfig;

/**
 * Main class for the load balancing proxy server.
 *
 * @author Jakob Korherr
 */
public class LoadBalancerProxyServer {

    public static String ERROR_RESPONSE =
            "HTTP/1.1 503 Service Unavailable\n" +
            "Server: Apache-Coyote/1.1\n" +
            "Content-Type: text/html\n" +
            "Connection: close\n" +
            "\n" +
            "<!DOCTYPE HTML>\n" +
            "<html>\n" +
                    "<head><title>Service Temporarily Unavailable</title></head>\n" +
                    "<body>Service Temporarily Unavailable</body>\n" +
            "</html>\n";

    public static void main(String[] args) {
        TcpProxyConfig config = new TcpProxyConfig(8000);
        config.setWorkerCount(Runtime.getRuntime().availableProcessors());

        new TcpProxy(config, new RoundRobinServerSelector()).start();
    }


}
