package cs451.Utils;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    public byte[] payload;
    public boolean flag = Constant.SEND;
    public Message(byte[] payload){
        this.payload = payload;
    }

    public void toAck(){
        this.flag = Constant.ACK;
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
        return new String(this.payload).hashCode();
    }
}
