package cs451.Utils;

import cs451.Host;

import java.util.List;

public class Constant {
    public static final String BROADCAST = "b";
    public static final String DELIVER = "d";
    public static final int RECEIVEINTERVAL = 300; //Interval between sending same message
    public static final int SENDINTERVAL = 300;
    public static final int NOTFOUND = -1;
    public static final int BUFFER_SIZE = 30;

    public static final String getIpFromHosts(List<Host> hosts, int processId){
        return hosts.get(processId - 1).getIp();
    }

    public static final int getPortFromHosts(List<Host> hosts, int processId){
        return hosts.get(processId - 1).getPort();
    }

    public static final int getProcessIdFromIpAndPort(List<Host> hosts, String ip, int port){
        for (Host host:hosts){
            if (host.getIp().equals(ip) && host.getPort() == port)
                return host.getId();
        }
        return NOTFOUND;
    }
}
