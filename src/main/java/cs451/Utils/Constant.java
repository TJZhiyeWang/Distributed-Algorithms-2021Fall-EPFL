package cs451.Utils;

import cs451.Host;

import java.util.List;

public class Constant {
    public static final String BROADCAST = "b";
    public static final String DELIVER = "d";
    public static final int INTERVAL = 1000; //Interval between sending same message

    public static final String getIpFromHosts(List<Host> hosts, int processId){
        return hosts.get(processId - 1).getIp();
    }

    public static final int getPortFromHosts(List<Host> hosts, int processId){
        return hosts.get(processId - 1).getPort();
    }
}
