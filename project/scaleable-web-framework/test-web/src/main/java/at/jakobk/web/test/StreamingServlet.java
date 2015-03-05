package at.jakobk.web.test;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class StreamingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Content-type", "text/html; charset=utf-8");
        AsyncContext asyncContext = request.startAsync();

        DynamicContentWriter command = new DynamicContentWriter(asyncContext);
        ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(command, 2000, 2000, TimeUnit.MILLISECONDS);
        command.setFuture(scheduledFuture);
    }

    private static class DynamicContentWriter implements Runnable {

        private int i = 0;
        private final AsyncContext asyncContext;
        private ScheduledFuture future;

        private DynamicContentWriter(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        public void setFuture(ScheduledFuture future) {
            this.future = future;
        }

        @Override
        public void run() {
            try {
                ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream();
                outputStream.println("dynamic content " + i);
                outputStream.flush();
                asyncContext.getResponse().flushBuffer();
                if (i >= 10) {
                    outputStream.close();
                    asyncContext.complete();
                    if (future != null) {
                        future.cancel(false);
                    }
                } else {
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
