package impl.services.defaults.remote_service;

import api.event_queue.EventQueueInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServlet;

/**
 * Created by chris on 7/11/16.
 */
public abstract class ServiceServlet extends HttpServlet {
    protected Log log;
    protected EventQueueInterface eventQueueInterface;
    public ServiceServlet(EventQueueInterface eventQueueInterface) {
        log = LogFactory.getLog(getClass());
        this.eventQueueInterface = eventQueueInterface;
    }
}
