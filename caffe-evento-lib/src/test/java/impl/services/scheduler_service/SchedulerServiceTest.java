package impl.services.scheduler_service;

import api.event_queue.Event;
import api.event_queue.EventQueue;
import api.event_queue.EventQueueInterface;
import api.event_queue.EventSource;
import impl.event_queue.EventImpl;
import impl.event_queue.EventQueueInterfaceImpl;
import impl.event_queue.EventSourceImpl;
import impl.event_queue.SynchronousEventQueue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import test_util.EventCollector;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

/** TODO:Implement tests.
 * Created by eric on 7/15/16.
 */
@RunWith(PowerMockRunner.class)
public class SchedulerServiceTest {
    private EventQueue eventQueue = new SynchronousEventQueue();
    private EventQueueInterface eventQueueInterface = new EventQueueInterfaceImpl();
    private SchedulerService instance = new SchedulerService(eventQueueInterface);
    private EventCollector eventCollector = new EventCollector();
    private EventSource eventGenerator = new EventSourceImpl();

    @Before
    public void setUp() {
        eventQueue.addEventQueueInterface(eventQueueInterface);
        eventQueue.addEventHandler(eventCollector.getHandler());
        eventQueue.addEventSource(eventGenerator);
    }

    @Test
    public void testScheduleEvent() throws Exception {
        Event scheduledEvent = new EventImpl("Test Schedule Doer", "TestReq");

        //Clunky at best
        Map<String, String> params = new HashMap<>();
        params.put(SchedulerService.SCHEDULER_TIME_FIELD, Date.from(Instant.now()).toString());
        Event schedulerEvent = SchedulerService.generateSchedulerEvent("Test Schedule", scheduledEvent, params);

        eventGenerator.registerEvent(schedulerEvent);
        sleep(100);
        assertEquals(1, eventCollector.findEventsWithName("Test Schedule Doer").size());
    }

    @Test
    public void testCancelEvent() throws Exception {
        throw new Exception("Test not implemented.");
    }
}