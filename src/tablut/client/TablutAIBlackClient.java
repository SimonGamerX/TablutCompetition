package tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutAIBlackClient {

    public static void main(String[] args) throws UnknownHostException, IOException {
        String name = "AIBlack";
        int timeout = 60;
        String ipAddress = "localhost";

        if (args.length > 0) {
            timeout = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            ipAddress = args[1];
        }

        TablutAIClient client = new TablutAIClient("BLACK", name, timeout, ipAddress);
        client.run();
    }
}
