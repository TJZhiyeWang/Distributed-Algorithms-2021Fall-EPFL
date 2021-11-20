package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.io.*;
import java.net.*;
import java.util.Arrays;

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
            String message = String.valueOf(m.payload) + ":" + String.valueOf(m.sProcess) +
                    ":" + String.valueOf(m.seq) + ":" + String.valueOf(m.flag);
            byte[] bytes = message.getBytes();
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
            Message m = new Message(Integer.parseInt(messages[0]),Integer.parseInt(messages[1]),Integer.parseInt(messages[2]));
            Record record = new Record(m, Constant.getProcessIdFromIpAndPort(packet.getAddress().getHostAddress(), packet.getPort()));
            return record;
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            socket.receive(packet);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream (packet.getData());
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            try {
                Object obj = inputStream.readObject();
                if (obj instanceof Message) {
                    Record record = new Record((Message) obj, Constant.getProcessIdFromIpAndPort(packet.getAddress().getHostAddress(), packet.getPort()));
                    return record;
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
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
