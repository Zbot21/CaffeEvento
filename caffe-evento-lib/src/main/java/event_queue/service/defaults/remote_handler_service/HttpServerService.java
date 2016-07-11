package event_queue.service.defaults.remote_handler_service;

import event_queue.service.Service;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by chris on 7/11/16.
 */
public class HttpServerService extends Service {
    public static int DEFAULT_LISTEN_PORT = 43215;

    private Server server;
    private ServletHandler servletHandler;

    public HttpServerService(int port) {
        server = new Server(port);
        servletHandler = new ServletHandler();
        server.addHandler(servletHandler);
        addServiceServlet(AddEventHandlerServlet.class, "/addEventHandler");
        addServiceServlet(RegisterEventServlet.class, "/registerEventServlet");
        addServiceServlet(RemoveEventHandlerServlet.class, "/removeEventHandler");
    }

    protected void addServiceServlet(Class<? extends ServiceServlet> clazz, String mapping) {
        try {
            ServiceServlet servlet = (ServiceServlet)clazz.getConstructor(Service.class).newInstance(this);
            ServletHolder servletHolder = new ServletHolder(servlet);
            servletHandler.addServletWithMapping(servletHolder, mapping);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("Could not add service servlet " + clazz.getName(), e);
        }
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                try {
                    server.start();
                    server.join();
                } catch (Exception e) {
                    log.error("There was an error trying to run the server.", e);
                }

            }
        }.start();
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            log.error("There was an error trying to shut down the server", e);
        }
    }


}
