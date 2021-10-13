package cs451.Utils;

public class Record {
    public Message m;
    public String ipAddress;
    public int port;

    public Record(Message m, String ipAddress, int port){
        this.m = m;
        this.ipAddress = ipAddress;
        this.port = port;
    }
}
