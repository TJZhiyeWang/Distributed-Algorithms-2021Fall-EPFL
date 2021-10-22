package cs451.Utils;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    public byte[] payload;
    public Message(byte[] payload){
        this.payload = payload;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Message){
            return (Arrays.equals(this.payload, ((Message) obj).payload));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.payload.length;
    }
}
