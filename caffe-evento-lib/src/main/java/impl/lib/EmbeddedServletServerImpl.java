package impl.lib;

import api.lib.EmbeddedServletServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Created by chris on 7/14/16.
 */
public final class EmbeddedServletServerImpl implements EmbeddedServletServer {
    private ServletContextHandler contextHandler;
    private Log log;

    public EmbeddedServletServerImpl(ServletContextHandler contextHandler) {
        this.contextHandler = contextHandler;
        log = LogFactory.getLog(getClass());
    }

    public void addServletConsumer(String endpoint, BiConsumer<HttpServletRequest, HttpServletResponse> consumer) {
        Servlet s = new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse res)
                    throws ServletException, IOException {
                consumer.accept(req, res);
            }
        };
        ServletHolder servletHolder = new ServletHolder(s);
        contextHandler.addServlet(servletHolder, endpoint);
    }
}
