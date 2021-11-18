package cs451.Utils;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.Arrays;

public class Message implements Serializable {
    public byte[] payload;
    public int sProcess;
    public int seq;
    public boolean flag = Constant.SEND;
    public Message(byte[] payload, int sProcess, int seq){
        this.payload = payload;
        this.sProcess = sProcess;
        this.seq = seq;
    }

    public void revert(){
        this.flag = this.flag==Constant.ACK? Constant.SEND : Constant.ACK;
    }


    @Override
    public boolean equals(Object obj){
        if (obj instanceof Message){
            return new String(this.payload).equals(new String(((Message) obj).payload)) && this.sProcess == ((Message) obj).sProcess;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new String(this.payload).hashCode() + this.sProcess;
    }
}
