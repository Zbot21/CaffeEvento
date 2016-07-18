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

import static java.lang.Thread.sleep;
import static java.time.temporal.ChronoUnit.SECONDS;
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
        params.put(SchedulerService.START_TIME, Date.from(Instant.now().plus(1, SECONDS)).toString());
        Event schedulerEvent = SchedulerService.generateSchedulerEvent("Test Schedule", scheduledEvent, params);

        eventGenerator.registerEvent(schedulerEvent);
        assertEquals("unregistered scheduler too early", 1, instance.numberOfActiveSchedulers());
        assertEquals("registered scheduledEvent too early", 0, eventCollector.findEventsWithName("Test Schedule Doer").size());
        sleep(1100);
        assertEquals("activeScheduler not removed", 0, instance.numberOfActiveSchedulers());
        assertEquals(1, eventCollector.findEventsWithName("Test Schedule Doer").size());
    }

    @Test
    public void testCancelEvent() throws Exception {
        Event scheduledEvent = new EventImpl("Test Schedule Doer", "TestReq");

        //Clunky at best
        Map<String, String> params = new HashMap<>();
        params.put(SchedulerService.START_TIME, Date.from(Instant.now().plus(1, SECONDS)).toString());
        Event schedulerEvent = SchedulerService.generateSchedulerEvent("Test Schedule", scheduledEvent, params);
        Event cancelEvent = SchedulerService.generateSchedulerCancelEvent("Test Schedule Cancel", UUID.fromString(schedulerEvent.getEventField(SchedulerService.SCHEDULE_ID_FIELD)));

        eventGenerator.registerEvent(schedulerEvent);
        assertEquals("unregistered scheduler too early", 1, instance.numberOfActiveSchedulers());
        assertEquals("registered scheduledEvent too early", 0, eventCollector.findEventsWithName("Test Schedule Doer").size());
        sleep(40);
        eventGenerator.registerEvent(cancelEvent);
        assertEquals("activeScheduler not removed", 0, instance.numberOfActiveSchedulers());
        assertEquals("Event fired when canceled1", 0, eventCollector.findEventsWithName("Test Schedule Doer").size());
        sleep(1000);
        assertEquals("Event fired when canceled2", 0, eventCollector.findEventsWithName("Test Schedule Doer").size());
        assertEquals("Canceled Event did not fire", 1, eventCollector.findEventsWithType(SchedulerService.SCHEDULE_EVENT_CANCELED).size());
    }
}