package event_queue.service.defaults.remote_service.client;

import event_queue.EventHandler;
import event_queue.service.Service;

/**
 * Created by chris on 7/12/16.
 */
public class HttpClientService extends Service {

    @Override
    public void addEventHandler(EventHandler handler) {
        super.addEventHandler(handler);
        // Check if this is a remote handled connection?
    }

    @Override
    public void removeEventHandler(EventHandler handler) {

    }
}
