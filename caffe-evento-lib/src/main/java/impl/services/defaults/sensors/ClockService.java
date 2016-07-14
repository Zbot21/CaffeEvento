package impl.services.defaults.sensors;

import api.event_queue.EventQueueInterface;
import api.event_queue.EventSource;
import impl.event_queue.EventSourceImpl;
import impl.services.AbstractService;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by chris on 7/4/16.
 */
public class ClockService extends AbstractService {
    // One of those moments when service != action and I need to think what a clock service actually does before I figure out how to implement it
    public static final String CLOCK_EVENT_TYPE = "CLOCK";
    public static final String CLOCK_EVENT_FUFILLMENT = "CLOCK_TIME";

    /* Clock Types */
    public static final String CLOCK_SET_EVENT = "CLOCK_SET";
    public static final String CLOCK_GET_EVENT = "CLOCK_GET";
    public static final String CLOCK_ALARM_SET = "CLOCK_ALARM_SET";
    public static final String CLOCK_TIMER_START = "CLOCK_TIMER_START";
    public static final String CLOCK_TIMER_STOP = "CLOCK_TIMER_STOP";

    /* Clock Fields */
    public static final String ALARM_ID_FIELD = "CLOCK_ALARM_ID";
    public static final String TIMER_ID_FIELD = "CLOCK_TIMER_ID";

    private final EventSource eventGenerator = new EventSourceImpl();
    private final Map<UUID, Timer> activeTimers= new HashMap<>();

    private static final Log log = LogFactory.getLog(ClockService.class);

    public ClockService(EventQueueInterface eventQueueInterface)
    {
        super(eventQueueInterface);
        getEventQueueInterface().addEventSource(eventGenerator);

        //Todo: Add handlers to eventQueueInterface
        //Todo: Add EventSources to the eventQueueInterface
    }

    private class Timer{

    }
}
