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
