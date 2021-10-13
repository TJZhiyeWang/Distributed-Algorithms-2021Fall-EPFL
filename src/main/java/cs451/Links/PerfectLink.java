package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Logger;
import cs451.Utils.Message;

import java.util.HashSet;
import java.util.Set;

public class PerfectLink implements Link{
    Set delivered;
    StubbornLink stubbornLink;
    Logger logger;
    PerfectLink(int port, Logger logger){
        this.stubbornLink = new StubbornLink(port);
        this.delivered = new HashSet<Message>();
        this.logger = logger;
    }
    @Override
    public void send(Message m, String ip, int port){
        String record = Constant.BROADCAST + " " + m.payload + "\n";
        logger.write(record);
        stubbornLink.send(m, ip, port);
    }

    @Override
    public Message receive(){
        return stubbornLink.receive();
    }

    @Override
    public Message deliver(Message m){
        if (delivered.contains(m))
            return null;
        return m;
    }
}
