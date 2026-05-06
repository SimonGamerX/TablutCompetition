package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutAI {

    public static void main(String[] args) throws UnknownHostException, IOException {
        String role = "";
        int timeout = 60;
        String ipAddress = "localhost";
        String name = "Ulisse_Nicola"; // Change this to your actual group name!

        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            role = args[0];
        }

        if (args.length >= 2) {
            timeout = Integer.parseInt(args[1]);
        }

        if (args.length >= 3) {
            ipAddress = args[2];
        }

        System.out.println("Starting AI as " + role + " with timeout " + timeout + "s on server " + ipAddress);
        TablutAIClient client = new TablutAIClient(role.toUpperCase(), name, timeout, ipAddress);
        client.run();
    }
}
