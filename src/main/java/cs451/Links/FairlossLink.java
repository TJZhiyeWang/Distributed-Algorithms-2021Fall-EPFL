package cs451.Links;

import cs451.Host;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.io.*;
import java.net.*;
import java.util.List;
public class FairlossLink implements Link{

    DatagramSocket socket;
    List hosts;

    public FairlossLink(int port, List hosts){
        try {
            this.socket = new DatagramSocket(port);
            int size = socket.getSendBufferSize();
            socket.setReceiveBufferSize(hosts.size() * size);
            this.hosts = hosts;

        } catch (SocketException e){
            e.printStackTrace();
        }
    }

    /* use udp to send a packet */
    @Override
    public void send(Message m, String ip, int port) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();;
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
            try{
                Object obj = inputStream.readObject();
                if (obj instanceof Message){
                    Record record = new Record((Message) obj, packet.getAddress().getHostAddress(), packet.getPort());
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
    public Record deliver(Record m) { return m; }

    @Override
    public void close(){ socket.close(); }

}
