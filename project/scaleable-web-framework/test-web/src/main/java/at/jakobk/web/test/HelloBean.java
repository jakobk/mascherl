package at.jakobk.web.test;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@ServerEndpoint("/hello")
public class HelloBean {

    @OnMessage
    public String sayHello(String name, Session session) {
        return "Hello " + name + "!";
    }
}