package event_queue.service.defaults.remote_service_server;

import event_queue.EventGenerator;
import event_queue.EventQueue;
import event_queue.EventQueueImpl;
import event_queue.service.defaults.remote_service.server.HttpServerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test_util.EventCollector;

import static org.junit.Assert.*;

/**
 * Created by chris on 7/12/16.
 */
public class HttpServerServiceTest {
    private EventQueue eventQueue = new EventQueueImpl();
    private HttpServerService instance = new HttpServerService(2345);
    private EventCollector eventCollector = new EventCollector();
    private EventGenerator eventGenerator = new EventGenerator();

    @Before
    public void setUp() throws Exception{
        eventQueue.registerService(instance);
        eventQueue.addEventHandler(eventCollector.getHandler());
        eventQueue.addEventSource(eventGenerator);
        instance.start();
        Thread.sleep(50);
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(50);
        instance.stop();
    }

    @Test
    public void testAddEventHandler() {
        fail();
    }


}