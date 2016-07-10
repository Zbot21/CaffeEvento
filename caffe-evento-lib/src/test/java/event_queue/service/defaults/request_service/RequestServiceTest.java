package event_queue.service.defaults.request_service;

import event_queue.Event;
import event_queue.EventGenerator;
import event_queue.EventQueue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import test_util.EventCollector;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by chris on 7/9/16.
 */
@RunWith(PowerMockRunner.class)
public class RequestServiceTest {
    private EventQueue eventQueue = new EventQueue();
    private RequestService instance = new RequestService();
    private EventCollector eventCollector = new EventCollector();
    private EventGenerator eventGenerator = new EventGenerator();

    @Before
    public void setUp() {
        eventQueue.registerService(instance);
        eventQueue.addEventHandler(eventCollector);
        eventQueue.addEventSource(eventGenerator);
    }

    @Test
    public void testInitialRequest() {
        Event fufillerEvent = new Event("Test Request Doer", "TestReq");
        Event requestEvent = RequestService.generateRequestEvent("Test Request", fufillerEvent);
        eventGenerator.registerEvent(requestEvent);
        assertEquals(1, eventCollector.findEventsWithName("Test Request Doer").size());
        assertEquals(1, instance.numberOfActiveRequests());
    }

    @Test
    public void testRequestFufilled() {
        Event fufillerEvent = new Event("Test Request Doer", "TestReq");
        Event requestEvent = RequestService.generateRequestEvent("Test Request", fufillerEvent);
        UUID requestId = UUID.fromString(requestEvent.getEventField(RequestService.REQUEST_ID_FIELD));
        eventGenerator.registerEvent(requestEvent);
        assertEquals(1, instance.numberOfActiveRequests());
        eventGenerator.registerEvent(RequestService.generateRequestSuccessEvent("Request Success!", requestId));
        assertEquals(0, instance.numberOfActiveRequests());
        assertEquals(1, eventCollector.findEventsWithType(RequestService.REQUEST_COMPLETED_EVENT).size());
    }

    @Test
    public void testRequestFailed() {
        Event fufillerEvent = new Event("Test Request Doer", "TestReq");
        Event requestEvent = RequestService.generateRequestEvent("Test Request", fufillerEvent);
        UUID requestId = UUID.fromString(requestEvent.getEventField(RequestService.REQUEST_ID_FIELD));
        eventGenerator.registerEvent(requestEvent);
        assertEquals(1, instance.numberOfActiveRequests());
        eventGenerator.registerEvent(RequestService.generateRequestFailedEvent("Request Failed :-(", requestId));
        assertEquals(1, instance.numberOfActiveRequests());
        assertEquals(2, eventCollector.findEventsWithName("Test Request Doer").size());
    }

    @Test
    public void testFailuresMoreThanMax() {
        Event fufillerEvent = new Event("Test Request Doer", "TestReq");
        Event requestEvent = RequestService.generateRequestEvent("Test Request", fufillerEvent);
        UUID requestId = UUID.fromString(requestEvent.getEventField(RequestService.REQUEST_ID_FIELD));
        eventGenerator.registerEvent(requestEvent);
        assertEquals(1, instance.numberOfActiveRequests());
        for(int i = 0; i < RequestService.MAX_RETRIES; i++){
            assertEquals(i + 1, eventCollector.findEventsWithName("Test Request Doer").size());
            eventGenerator.registerEvent(RequestService.generateRequestFailedEvent("Request Failed :-(", requestId));
            assertEquals(1, instance.numberOfActiveRequests());
            assertEquals(i + 2, eventCollector.findEventsWithName("Test Request Doer").size());
        }
        eventGenerator.registerEvent(RequestService.generateRequestFailedEvent("Final Request Failed :-(", requestId));
        assertEquals(0, instance.numberOfActiveRequests());
    }


}