package cs451.Utils;

public class Record {
    public Message m;
    public int i;

    public Record(Message m, int i){
        this.m = m;
        this.i = i;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Record){
            Record tmp = (Record) obj;
            return (this.m.equals(tmp.m) && this.i == tmp.i);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.m.hashCode() + this.i;
    }
}
