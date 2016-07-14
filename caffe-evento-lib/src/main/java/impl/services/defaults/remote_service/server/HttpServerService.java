package impl.services.defaults.remote_service.server;

import api.event_queue.EventQueueInterface;
import impl.services.AbstractService;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;

/**
 * Created by chris on 7/11/16.
 */
public class HttpServerService extends AbstractService {
    public static int DEFAULT_LISTEN_PORT = 43215;

    private Server server;
    private ServletHandler servletHandler;
    public HttpServerService(EventQueueInterface eventQueueInterface) {
        this(eventQueueInterface, DEFAULT_LISTEN_PORT);
    }

    public HttpServerService(EventQueueInterface eventQueueInterface, int port) {
        super(eventQueueInterface);
        server = new Server(port);
        servletHandler = new ServletHandler();
        server.addHandler(servletHandler);
//        addServiceServlet(AddEventHandlerServlet.class, ADD_EVENT_HANDLER_ENDPOINT);
//        addServiceServlet(RegisterEventServlet.class, REGISTER_EVENT_ENDPOINT);
//        addServiceServlet(RemoveEventHandlerServlet.class, REMOVE_EVENT_HANDLER_ENDPOINT);
    }

//    protected void addServiceServlet(Class<? extends ServiceServlet> clazz, String mapping) {
//        try {
//            ServiceServlet servlet = clazz.getConstructor(EventQueueInterfaceImpl.class).newInstance(this);
//            ServletHolder servletHolder = new ServletHolder(servlet);
//            servletHandler.addServletWithMapping(servletHolder, mapping);
//        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            log.error("Could not add eventQueueInterface servlet " + clazz.getName(), e);
//        }
//    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                try {
                    server.start();
                    log.info("Server started.");
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
            log.info("Server stopped");
        } catch (Exception e) {
            log.error("There was an error trying to shut down the server", e);
        }
    }


}
