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
import java.util.Date;

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
    public void testGenerateSchedulerEvent() throws Exception {
        throw new Exception("Test not implemented.");
    }

    @Test
    public void testGenerateSchedulerCancelEvent() throws Exception {
        throw new Exception("Test not implemented.");
    }

    @Test
    public void testScheduleEvent() throws Exception {
        Event scheduledEvent = new EventImpl("Test Schedule Doer", "TestReq");
        Event schedulerEvent = SchedulerService.generateSchedulerEvent("Test Schedule", scheduledEvent, Date.from(Instant.now()));
        eventGenerator.registerEvent(schedulerEvent);
        assertEquals(1, eventCollector.findEventsWithName("Test Schedule Doer").size());
        throw new Exception("Test not implemented.");
    }

    @Test
    public void testCancelEvent() throws Exception {
        throw new Exception("Test not implemented.");
    }
}