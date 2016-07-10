package server;

import event_queue.EventQueue;
import org.mortbay.jetty.Server;

/**
 * Created by chris on 7/10/16.
 */
public class WebEngine {
    private EventQueue queue;
    private Server server;

    /* TODO
       This web interface needs to be able to
       - Add local jars (services)
       - Add remote services (through web API calls)
       - Register remote events

       Potential Steps:
       - Create a way to register a server service (local jar)
       - The server service needs to provide:
        - an endpoint for future events (this could be a client of some kind)
        - it needs to be provided with, an endpoint for sending its data (the events that it generates)

       - So really, we need a very simple way to add:
        - Events to the queue (on the server side)
        - Servers and handlers for the servers
        - Need to be able to add/remove them


     */
}
