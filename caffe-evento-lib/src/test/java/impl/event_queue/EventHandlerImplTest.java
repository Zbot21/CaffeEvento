package impl.event_queue;

import api.event_queue.Event;
import api.event_queue.EventHandler;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Created by chris on 7/13/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Event.class, EventHandlerImpl.class})
public class EventHandlerImplTest {

    @Mock
    Event event;

    @Test
    public void testEventHandlerEventData() {
        EventHandler handler = EventHandler.create().eventData("TEST_KEY", "TEST_VALUE").build();
        expect(event.getEventField("TEST_KEY")).andReturn("TEST_VALUE").once();

        replayAll();
        assertEquals(true, handler.getHandlerCondition().test(event));
        verifyAll();
    }

    @Test
    public void testMultiEventHandlerEventDataPass() {
        EventHandler handler = EventHandler.create()
                .eventData("TEST_KEY1", "TEST_VALUE1")
                .eventData("TEST_KEY2", "TEST_VALUE2").build();
        expect(event.getEventField("TEST_KEY1")).andReturn("TEST_VALUE1").once();
        expect(event.getEventField("TEST_KEY2")).andReturn("TEST_VALUE2").once();

        replayAll();
        assertEquals(true, handler.getHandlerCondition().test(event));
        verifyAll();
    }

    @Test
    public void testMultiEventHandlerEventDataFail() {
        EventHandler handler = EventHandler.create()
                .eventData("TEST_KEY1", "TEST_VALUE1")
                .eventData("TEST_KEY2", "TEST_VALUE").build();
        expect(event.getEventField("TEST_KEY1")).andReturn("TEST_VALUE1").once();
        expect(event.getEventField("TEST_KEY2")).andReturn("TEST_VALUE2").once();

        replayAll();
        assertEquals(false, handler.getHandlerCondition().test(event));
        verifyAll();
    }

    @Test
    public void testWrongEventData() {
        EventHandler handler = EventHandler.create().eventData("TEST_KEY", "TEST_VALUE").build();
        expect(event.getEventField("TEST_KEY")).andReturn("WRONG_VALUE").once();

        replayAll();
        assertEquals(false, handler.getHandlerCondition().test(event));
        verifyAll();
    }


    @Test
    public void testEventHandlerEventType() {
        EventHandler handler = EventHandler.create().eventType("TEST_TYPE").build();
        expect(event.getEventType()).andReturn("TEST_TYPE").once();

        replayAll();
        assertEquals(true, handler.getHandlerCondition().test(event));
        verifyAll();
    }

    @Test
    public void testEventHandlerWrongType() {
        EventHandler handler = EventHandler.create().eventType("TEST_TYPE1").build();
        expect(event.getEventType()).andReturn("TEST_TYPE").once();

        replayAll();
        assertEquals(false, handler.getHandlerCondition().test(event));
        verifyAll();
    }

    @Test
    public void testEventHandlerEventName() {
        EventHandler handler = EventHandler.create().eventType("NAME_TEST").build();
        expect(event.getEventType()).andReturn("NAME_TEST").once();

        replayAll();
        assertEquals(true, handler.getHandlerCondition().test(event));
        verifyAll();
    }

    @Test
    public void testJsonEventHandlerTransmission() {
        EventHandler original = EventHandler.create().eventName("NAME").eventType("TYPE").eventData("DATA_KEY", "DATA_VALUE").build();
        EventHandler copied = EventHandler.fromJson(original.encodeToJson());
        expect(event.getEventName()).andReturn("NAME").anyTimes();
        expect(event.getEventType()).andReturn("TYPE").anyTimes();
        expect(event.getEventField("DATA_KEY")).andReturn("DATA_VALUE").anyTimes();

        replayAll();
        assertEquals(true, original.getHandlerCondition().test(event));
        assertEquals(true, copied.getHandlerCondition().test(event));
        assertEquals(original.getEventHandlerId(), copied.getEventHandlerId());
        verifyAll();
    }

    @Test
    public void testJsonEventHandlerTransmissionNonMatchingEvent() {
        EventHandler original = EventHandler.create().eventName("NAME").eventType("TYPE").eventData("DATA_KEY", "DATA_VALUE").build();
        String eventHandlerJson = original.encodeToJson();
        EventHandler copied = EventHandler.fromJson(eventHandlerJson);
        expect(event.getEventName()).andReturn("NAME1").anyTimes();
        expect(event.getEventType()).andReturn("TYPE").anyTimes();
        expect(event.getEventField("DATA_KEY")).andReturn("DATA_VALUE").anyTimes();

        replayAll();
        assertEquals(false, original.getHandlerCondition().test(event));
        assertEquals(false, copied.getHandlerCondition().test(event));
        assertEquals(original.getEventHandlerId(), copied.getEventHandlerId());
        verifyAll();
    }

    @Test
    public void testEventHandlerJsonTransmissionDataEqual() {
        EventHandler original = EventHandler.create().eventName("NAME").eventType("TYPE").eventData("DATA_KEY", "DATA_VALUE").build();
        String eventHandlerJson = original.encodeToJson();
        EventHandler copied = EventHandler.fromJson(eventHandlerJson);
        String copiedJson = copied.encodeToJson();
        assertEquals(eventHandlerJson, copiedJson);
    }
}
