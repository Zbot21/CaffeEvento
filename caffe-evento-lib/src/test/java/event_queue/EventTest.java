package event_queue;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by chris on 7/1/16.
 */
public class EventTest {
    Event instance;

    @org.junit.Before
    public void setUp() throws Exception {
        instance = new Event();
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConstructor() {
        instance = new Event("The Event Name", "The Event Type");
        assertEquals("The Event Name", instance.getEventName());
        assertEquals("The Event Type", instance.getEventType());
    }

    @Test
    public void testEventName() {
        instance.setEventName("The Event Name");
        assertEquals("The Event Name", instance.getEventName());
    }

    @Test
    public void testEventType() {
        instance.setEventType("The Event Type");
        assertEquals("The Event Type", instance.getEventType());
    }

    @Test
    public void testEventData() {
        instance.setEventField("Event Field 1", "Event Data 1");
        assertEquals("Event Data 1", instance.getEventField("Event Field 1"));
    }

    @Test
    public void testEventDataWithNoDataReturnsNull() {
        assertEquals(null, instance.getEventField("Event Field 1s"));
    }

    @Test
    public void testEventEncodeDecode() {
        instance = new Event("The Event Name", "Test Event Type");
        instance.setEventField("EventField1", "EventField1");
        instance.setEventField("EventField2", "EventField2");
        String encoded = instance.encodeEvent();
        Event decoded  = Event.decodeEvent(encoded);
        assertEventsEqual(instance, decoded);
    }

    public void assertEventsEqual(Event e1, Event e2){
        assertEquals(e1.getEventName(), e2.getEventName());
        assertEquals(e1.getEventName(), e2.getEventName());
        assertTrue(e1.getEventDetails().entrySet().containsAll(e2.getEventDetails().entrySet()));
        assertTrue(e2.getEventDetails().entrySet().containsAll(e1.getEventDetails().entrySet()));
    }
}