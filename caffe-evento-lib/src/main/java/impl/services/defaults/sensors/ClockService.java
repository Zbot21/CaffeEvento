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

/** TODO: Fix or replace clockservice
 * Created by chris on 7/4/16.
 */
public class ClockService extends AbstractService {
    /* Clock Events */
    public static final String CLOCK_EVENT_TYPE = "CLOCK";
    public static final String CLOCK_EVENT_RESPONSE = "CLOCK_TIME";

    /* Clock Types */
    public static final String CLOCK_SET_EVENT = "CLOCK_SET";
    public static final String CLOCK_GET_EVENT = "CLOCK_GET";

    /* Clock Fields */
    public static final String CLOCK_TIME = "TIME";
    public static final String CLOCK_DATE = "DATE";

    private final EventSource eventGenerator = new EventSourceImpl();

    private static final Log log = LogFactory.getLog(ClockService.class);

    public ClockService(EventQueueInterface eventQueueInterface)
    {
        super(eventQueueInterface);
        getEventQueueInterface().addEventSource(eventGenerator);

        //Todo: Add handlers to eventQueueInterface
        //Todo: Add EventSources to the eventQueueInterface
    }

    private class Time{

    }
}
