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

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Record){
            Record tmp = (Record) obj;
            return (this.m.equals(tmp.m) && this.port == tmp.port && this.ipAddress.equals(tmp.ipAddress) );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.port + this.ipAddress.hashCode() + this.m.hashCode();
    }
}
