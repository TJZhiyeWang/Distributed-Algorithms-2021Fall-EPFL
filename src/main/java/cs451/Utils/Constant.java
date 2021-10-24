package cs451.Utils;

import cs451.Host;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.List;

public class Constant {
    public static final String BROADCAST = "b";
    public static final String DELIVER = "d";
    public static final int RECEIVEINTERVAL = 5; //Interval between sending same message
    public static final int SENDINTERVAL = 500;
    public static final int NOTFOUND = -1;
    public static final int BUFFER_SIZE = 1000000;
    public static final int BIG_NUMBER = 100000;
    public static final int HASHSET_CAPACITY = (int)Math.pow(2,10);
    public static final int SEND_MESSAGE = 5000;

    public static final boolean SEND = true;
    public static final boolean ACK = false;

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

//    public static Message parseByteStreamToMessage(DatagramPacket packet){
//        byte[] stream = packet.getData();
//        boolean flag = stream[packet.getLength() - 1]==(byte) 1? Constant.SEND: Constant.ACK;
//        Message m = new Message(Arrays.copyOf(stream, packet.getLength()-1));
//        if (flag) {
//            return m;
//        } else{
//            m.toACK();
//            return m;
//        }
//    }

    public static byte[] intToByteArray(int integer) {
        int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
        byte[] byteArray = new byte[4];
        for (int n = 0; n < byteNum; n++)
            byteArray[3 - n] = (byte) (integer>>> (n * 8));
        return (byteArray);
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        int value= 0;
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
}
