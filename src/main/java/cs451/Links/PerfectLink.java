package cs451.Links;

import cs451.Listener.Listener;
import cs451.Utils.Constant;
import cs451.Utils.Logger;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerfectLink implements Link{
    Set delivered;
    StubbornLink stubbornLink;
    Logger logger;
    Listener listener;
    public PerfectLink(int port, Logger logger, List hosts){
        this.stubbornLink = new StubbornLink(port);
        this.delivered = new HashSet<Message>();
        this.logger = logger;
        this.listener = new Listener(this, logger, hosts);
    }

    @Override
    public void send(Message m, String ip, int port){
        String log = Constant.BROADCAST + " " + m.payload + "\n";
        logger.log(log);
        System.out.println(log);
        stubbornLink.queue.offer(new Record(m, ip, port));
    }

    @Override
    public Record receive(){
        return stubbornLink.receive();
    }

    @Override
    public Record deliver(Record record){
        if (delivered.contains(record.m)) {
            return null;
        } else {
            delivered.add(record.m);
        }
        return record;
    }

    @Override
    public void close(){
        this.stubbornLink.t.interrupt();
        this.listener.stop();
        stubbornLink.close();
    }
}
