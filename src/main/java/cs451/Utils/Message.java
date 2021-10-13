package cs451.Utils;

public class Message {
    public int srcProcess;
    public int payload;
    public Message(int srcProcess, int payload){
        this.payload = payload;
        this.srcProcess = srcProcess;
    }

}
