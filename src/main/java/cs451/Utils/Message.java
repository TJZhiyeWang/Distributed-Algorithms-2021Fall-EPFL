package cs451.Utils;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    public int payload;
    public boolean flag;
    public Message(int payload, boolean flag){
        this.payload = payload;
        this.flag = flag;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Message){
            return this.payload == ((Message) obj).payload;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.payload;
    }
}
