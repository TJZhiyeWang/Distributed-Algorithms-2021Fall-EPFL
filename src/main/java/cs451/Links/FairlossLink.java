package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.io.*;
import java.net.*;

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
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(m);
            outputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
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
        byte[] container = new byte[128];
        DatagramPacket packet = new DatagramPacket(container, 0, container.length);
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
