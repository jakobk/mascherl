package org.mascherl.example.filter;

import org.mascherl.example.domain.User;
import org.mascherl.page.Mascherl;
import org.mascherl.session.MascherlSession;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Objects;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
@Priority(Priorities.AUTHENTICATION)
public class WebMailRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (Objects.equals("GET", requestContext.getRequest().getMethod())) {
            MascherlSession session = MascherlSession.getInstance();
            User user = session.get("user", User.class);
            if (requestContext.getUriInfo().getPath().startsWith("mail"))  {
                if (user == null) {
                    if (isAjaxRequest(requestContext)) {
                        requestContext.abortWith(Response.ok(Mascherl.navigate("/").redirect()).build());
                    } else {
                        requestContext.abortWith(Response.seeOther(UriBuilder.fromUri("/").build()).build());
                    }
                }
            } else {
                if (user != null) {
                    if (isAjaxRequest(requestContext)) {
                        requestContext.abortWith(Response.ok(Mascherl.navigate("/mail").redirect()).build());
                    } else {
                        requestContext.abortWith(Response.seeOther(UriBuilder.fromUri("/mail").build()).build());
                    }
                }
            }
        }
    }

    private boolean isAjaxRequest(ContainerRequestContext requestContext) {
        return Objects.equals(requestContext.getHeaderString("X-Requested-With"), "XMLHttpRequest");
    }

}
