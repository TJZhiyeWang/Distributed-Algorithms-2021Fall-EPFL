package cs451;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class BarrierParser {

    private static final String BARRIER_KEY = "--barrier";

    private static final int BARRIER_ARGS_NUM = 2;
    private static final String COLON_REGEX = ":";
    private static final String IP_START_REGEX = "/";

    private static String ip;
    private static int port;

    public boolean populate(String key, String value) {
        if (!key.equals(BARRIER_KEY)) {
            return false;
        }

        String[] barrier = value.split(COLON_REGEX);
        if (barrier.length != BARRIER_ARGS_NUM) {
            return false;
        }

        try {
            String ipTest = InetAddress.getByName(barrier[0]).toString();
            if (ipTest.startsWith(IP_START_REGEX)) {
                ip = ipTest.substring(1);
            } else {
                ip = InetAddress.getByName(ipTest.split(IP_START_REGEX)[0]).getHostAddress();
            }

            port = Integer.parseInt(barrier[1]);
            if (port <= 0) {
                System.err.println("Barrier port must be a positive number!");
                return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return true;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
