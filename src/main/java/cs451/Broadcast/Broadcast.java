package cs451.Broadcast;

import cs451.Utils.Message;
import cs451.Utils.Record;

public interface Broadcast {

    public abstract void broadcast(Message m);

    public abstract Record receive();

    public abstract Record deliver();

}
