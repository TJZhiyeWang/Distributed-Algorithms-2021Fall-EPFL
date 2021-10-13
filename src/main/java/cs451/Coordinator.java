package cs451;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.Socket;
import java.net.UnknownHostException;

public class Coordinator {

    private int pid;

    private String barrierIp;
    private int barrierPort;

    private String signalIp;
    private int signalPort;

    private Socket signalSocket = null;

    public Coordinator(int pid, String barrierIp, int barrierPort, String signalIp, int signalPort) {
        this.pid = pid;
        this.barrierIp = barrierIp;
        this.barrierPort = barrierPort;
        this.signalIp = signalIp;
        this.signalPort = signalPort;

        signalSocket = connectToHost(this.signalIp, this.signalPort);
    }

    public void waitOnBarrier() {
        try {
	    Socket socket = connectToHost(barrierIp, barrierPort);
            InputStream input = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(input);
            System.out.println("Accessing barrier...");
            int character;
            while ((character = reader.read()) != -1) {}
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public void finishedBroadcasting() {
	try {
            signalSocket.close();
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private Socket connectToHost(String ip, int port) {
        Socket socket = null;
        try {
	    socket = new Socket(ip, port);
            OutputStream output = socket.getOutputStream();
            DataOutputStream writer = new DataOutputStream(output);

            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.putLong((long) pid);

            writer.write(bb.array(), 0, 8);
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }

        return socket;
    }
}
