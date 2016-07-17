package impl.lib;

import api.lib.EmbeddedServletServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

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
    public static int DEFAULT_LISTEN_PORT = 4325;
    private Server server;
    private ServletHandler servletHandler;
    private Log log;

    public EmbeddedServletServerImpl() {
        this(DEFAULT_LISTEN_PORT);
    }

    public EmbeddedServletServerImpl(int port) {
        server = new Server(port);
        servletHandler = new ServletHandler();
        server.addHandler(servletHandler);
        log = LogFactory.getLog(getClass());
    }

    @Override public void addServletConsumer(String endpoint,
                                   BiConsumer<HttpServletRequest, HttpServletResponse> consumer) {
        Servlet s = new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse res)
                    throws ServletException, IOException {
                consumer.accept(req, res);
            }
        };
        ServletHolder servletHolder = new ServletHolder(s);
        servletHandler.addServletWithMapping(servletHolder, endpoint);
    }

    @Override public void asyncStart() {
        new Thread() {
            @Override
            public void run() {
                syncStart();
            }
        }.start();
    }

    @Override public void syncStart() {
        if(!server.isStarted()){
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                log.error("There was an error trying to run the server.", e);
            }
        }
    }

    @Override public void stop() {
        // Safely stop the server from another thread.
        new Thread() {
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                    log.error("There was an error trying to shut down the server", e);
                }
            }
        }.start();

    }
}
