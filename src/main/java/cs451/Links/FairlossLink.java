package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

public class FairlossLink implements Link{

    DatagramSocket socket;

    public FairlossLink(int port){
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e){
            e.printStackTrace();
        }
    }

    /* use udp to send a packet */
    @Override
    public void send(Message m, String ip, int port) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append(m.payload);
            buffer.append(":");
            buffer.append(m.sProcess);
            buffer.append(":");
            buffer.append(m.flag);
            buffer.append(":");
            if (m.clock != null){
                for (int i = 0; i < m.clock.length; i++){
                    buffer.append(m.clock[i]);
                    buffer.append("_");
                }
            }
            byte[] bytes = buffer.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, new InetSocketAddress(ip, port));
            socket.send(packet);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /* receive but not deliver a packet
     */
    @Override
    public Record receive() {
        byte[] container = new byte[64];
        DatagramPacket packet = new DatagramPacket(container, 0, container.length);
        try {
            socket.receive(packet);
            byte[] bytes = Arrays.copyOf(packet.getData(), packet.getLength());
            String message = new String(bytes);
            String[] messages = message.split(":");
            Message m = new Message(Integer.parseInt(messages[0]), Integer.parseInt(messages[1]));
            if (messages[2].equals("true")){
                String[] numbers = messages[3].split("_");
                int[] clock = new int[numbers.length];
                for (int i = 0; i < clock.length; i++) {
                    clock[i] = Integer.parseInt(numbers[i]);
                }
                m.initClock(clock);
            }else{
                m.revert();
            }

            Record record = new Record(m, Constant.getProcessIdFromIpAndPort(packet.getAddress().getHostAddress(), packet.getPort()));
            return record;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;//if no message has been received
    }

    @Override
    public Record deliver() { return this.receive(); }

    @Override
    public void close(){ socket.close(); }

}
