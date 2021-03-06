/*
Copyright 2012 Artem Stasuk

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.mascherl.example.proxy;

import org.mascherl.example.balancer.RoundRobinServerSelector;
import org.mascherl.example.nio.TcpServer;
import org.mascherl.example.nio.TcpServerConfig;

/**
 * TCP proxy.
 * <p/>
 * After starting it listening local port and send all incoming
 * traffic on it from client to remote host and from remote host to client.
 * Doesn't have any timeout. If client or remote server closes connection it will
 * close opposite connection.
 * <p/>
 * Multi-thread and asynchronous TCP proxy server based on NIO.
 * <p/>
 * You can create any count of proxy instances and run they in together.
 *
 * @see TcpProxyConnectorFactory
 * @see TcpProxyConnector
 * @see TcpProxyConfig
 * @see TcpServer
 */
public class TcpProxy {

    private final TcpServer server;

    public TcpProxy(final TcpProxyConfig config, RoundRobinServerSelector serverSelector) {
        TcpProxyConnectorFactory handlerFactory = new TcpProxyConnectorFactory(config, serverSelector);

        final TcpServerConfig serverConfig =
                new TcpServerConfig(config.getLocalPort(), handlerFactory, config.getWorkerCount());

        server = new TcpServer(serverConfig);
    }

    /**
     * Start server.
     * This method run servers worked then return control.
     * This method isn't blocking.
     * <p/>
     * If you call this method when server is started, it throw exception.
     * <p/>
     * See {@link org.mascherl.example.nio.TcpServer#start()}
     */
    public void start() {
        server.start();
    }

    /**
     * Stop server and release all resources.
     * If server already been closed this method return immediately
     * without side effects.
     * <p/>
     * See {@link org.mascherl.example.nio.TcpServer#shutdown()}
     */
    public void shutdown() {
        server.shutdown();
    }

}
