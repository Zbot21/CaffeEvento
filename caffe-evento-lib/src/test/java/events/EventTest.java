package events;

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
    public void testConstructorEventName() {
        instance = new Event("The Event Name");
        assertEquals("The Event Name", instance.getEventName());
    }

    @Test
    public void testEventName() {
        instance.setEventName("The Event Name");
        assertEquals("The Event Name", instance.getEventName());
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
}