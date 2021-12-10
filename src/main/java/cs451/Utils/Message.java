package cs451.Utils;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.Arrays;

public class Message implements Serializable {
    public int payload;
    public int sProcess;
    public int[] clock;
    public boolean flag = Constant.SEND;
    public Message(int payload, int sProcess){
        this.payload = payload;
        this.sProcess = sProcess;
        this.clock = null;
    }

    public void revert(){
        this.flag = this.flag==Constant.ACK? Constant.SEND : Constant.ACK;
    }

    public void initClock (int[] clock){
        this.clock = clock;
    }

    public void destroyClock(){
        this.clock = null;
    }


    @Override
    public boolean equals(Object obj){
        if (obj instanceof Message){
            return this.payload == ((Message) obj).payload && this.sProcess == ((Message) obj).sProcess;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.payload + this.sProcess;
    }
}
