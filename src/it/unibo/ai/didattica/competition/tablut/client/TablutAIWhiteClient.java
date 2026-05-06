package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutAIWhiteClient {

    public static void main(String[] args) throws UnknownHostException, IOException {
        String name = "AIWhite";
        int timeout = 60;
        String ipAddress = "localhost";

        if (args.length > 0) {
            timeout = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            ipAddress = args[1];
        }

        TablutAIClient client = new TablutAIClient("WHITE", name, timeout, ipAddress);
        client.run();
    }
}
