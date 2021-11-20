package cs451.Utils;

import java.util.List;

public class Constant {
    public static final String BROADCAST = "b";
    public static final String DELIVER = "d";
    public static final int RECEIVEINTERVAL = 5; //Interval between sending same message
    public static final int SENDINTERVAL = 1000;
    public static final int NOTFOUND = -1;
    public static final int BUFFER_SIZE = 1000000;
    public static final int BIG_NUMBER = 100000;
    public static final int HASHSET_CAPACITY = (int)Math.pow(2,10);
    public static final int SEND_MESSAGE = 10000;

    public static final boolean SEND = true;
    public static final boolean ACK = false;
    public static List<Host> hosts;
    public static Logger logger;
    public static int myself;

    public static void initHost(List hosts){
        Constant.hosts = hosts;
    }

    public static void initlogger(Logger logger){
        Constant.logger = logger;
    }

    public static void initMyself(int me){
        Constant.myself = me;
    }

    public static int getMyself(){
        return myself;
    }

    public static Logger getLogger(){
        return logger;
    }


    public static List<Host> getHosts(){
        return hosts;
    }

    public static final String getIpFromHosts(int processId){
        return hosts.get(processId - 1).getIp();
    }

    public static final int getPortFromHosts(int processId){
        return hosts.get(processId - 1).getPort();
    }

    public static final int getProcessIdFromIpAndPort(String ip, int port){
        for (Host host:hosts){
            if (host.getIp().equals(ip) && host.getPort() == port)
                return host.getId();
        }
        return NOTFOUND;
    }

}
