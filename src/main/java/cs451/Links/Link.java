package cs451.Links;

import cs451.Utils.Message;

public interface Link {
    public abstract void send(Message m, String ip, int port);
    public abstract Message receive();
    public abstract Message deliver(Message m);
}
