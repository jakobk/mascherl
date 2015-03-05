package at.jakobk.web.test;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public class TestServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        AsyncContext context = request.startAsync();
        ServletInputStream input = request.getInputStream();
        MyReadListener readListener = new MyReadListener(input, context);
        input.setReadListener(readListener);
    }

    private static class MyReadListener implements ReadListener {

        private final ServletInputStream input;
        private final AsyncContext context;

        public MyReadListener(ServletInputStream input, AsyncContext context) {
            this.input = input;
            this.context = context;
        }

        @Override
        public void onDataAvailable() {
            System.out.println("onDataAvailable");
            try {
                StringBuilder sb = new StringBuilder();
                int len = -1;
                byte b[] = new byte[1024];
                while (input.isReady() && (len = input.read(b)) != -1) {
                    String data = new String(b, 0, len);
                    System.out.println("--> " + data);
                }
            } catch (IOException ex) {
                Logger.getLogger(MyReadListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void onAllDataRead() {
            System.out.println("onAllDataRead");
            context.complete();
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
            context.complete();
        }
    }

}
