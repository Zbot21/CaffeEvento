package server;

import org.mortbay.jetty.Server;

/**
 * Created by chris on 7/10/16.
 */
public class Main {
    public static void main(String[] args) {
        Server server = new Server(8080);
        System.out.println("Hello world");
    }
}
