package cs451.Utils;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.Arrays;

public class Message implements Serializable {
    public byte[] payload;
    public boolean flag = Constant.SEND;
    public Message(byte[] payload){
        this.payload = payload;
    }

    public void toACK(){
        this.flag = Constant.ACK;
    }

    public byte[] mergeToByteStream(){
        byte[] tmp = new byte[payload.length + 1];
        System.arraycopy(payload, 0, tmp, 0, payload.length);
        tmp[payload.length] = this.flag? (byte)1 : (byte)0;
        return tmp;
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
