package cs451.Utils;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.Arrays;

public class Message implements Serializable {
    public byte[] payload;
    public byte[] info;
    public int length;
    public boolean flag = Constant.SEND;
    public Message(byte[] payload){
        this.info = payload;
        this.length = payload.length;
        this.payload = new byte[payload.length + 1];
        System.arraycopy(payload, 0, this.payload,0,this.length);
        this.payload[payload.length] = this.flag? (byte)1 : (byte)0;

    }
    public Message(byte[] payload, boolean flag){
        this.info = Arrays.copyOf(payload, payload.length-1);
        this.payload = payload;
        this.length = payload.length - 1;
        this.flag = payload[payload.length - 1]==(byte) 1? Constant.SEND: Constant.ACK;
    }

    public void toACK(){
        this.flag = Constant.ACK;
        this.payload[payload.length - 1] = this.flag? (byte)1 : (byte)0;
    }


    @Override
    public boolean equals(Object obj){
        if (obj instanceof Message){
            return new String(this.info).equals(new String(((Message) obj).info));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new String(this.info).hashCode();
    }
}
