package integration;

import api.event_queue.EventQueueInterface;
import impl.event_queue.BufferedEventQueueInterface;

/**
 * Created by chris on 7/14/16.
 */
public class BufferedEventQueueInterfaceInteractionTest {
    private EventQueueInterface bufferedEventQueueInterface = new BufferedEventQueueInterface();
    private EventQueueInterface bufferedEventQueueInterface2 = new BufferedEventQueueInterface();
}
