package cs451.Utils;

import java.io.Serializable;

public class Message implements Serializable {
    public int payload;
    public Message(int payload){
        this.payload = payload;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Message){
            return (this.payload == ((Message) obj).payload);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.payload;
    }
}
