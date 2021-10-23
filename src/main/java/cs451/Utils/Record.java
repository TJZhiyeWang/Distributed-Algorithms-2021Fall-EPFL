package cs451.Utils;

public class Record {
    public Message m;
    public int port;
    public String ip;

    public Record(Message m, String ip, int port){
        this.m = m;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Record){
            Record tmp = (Record) obj;
            return (this.m.equals(tmp.m) && this.port == tmp.port && this.ip.equals(tmp.ip) );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.ip.hashCode() + this.m.hashCode() + this.port;
    }
}
