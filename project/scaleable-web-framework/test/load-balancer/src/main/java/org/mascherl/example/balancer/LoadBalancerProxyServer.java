package org.mascherl.example.balancer;

import org.mascherl.example.proxy.TcpProxy;
import org.mascherl.example.proxy.TcpProxyConfig;

/**
 * TODO
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
