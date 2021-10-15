package cs451.Utils;

import java.io.Serializable;

public class Message implements Serializable {
    public int srcProcess;
    public int payload;
    public Message(int srcProcess, int payload){
        this.payload = payload;
        this.srcProcess = srcProcess;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Message){
            return (this.payload == ((Message) obj).payload) && (this.srcProcess == ((Message) obj).srcProcess);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.payload + this.srcProcess;
    }
}
