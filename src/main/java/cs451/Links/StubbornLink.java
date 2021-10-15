package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class StubbornLink implements Link{

    Set sent;
    FairlossLink fairlossLink;
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                try{
                    for(Object obj : sent){
                        Record record = (Record) obj;
                        send(record);
                    }
                    Thread.sleep(Constant.INTERVAL);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    });

    public StubbornLink(int port) {
        this.fairlossLink = new FairlossLink(port);
        this.sent = new LinkedHashSet<Record>();
        t.start();
    }

    @Override
    public void send(Message m, String ip, int port){
        fairlossLink.send(m, ip, port);
        Record record = new Record(m, ip, port);
        sent.add(record);
    }

    public void send(Record record){
        fairlossLink.send(record.m, record.ipAddress, record.port);
    }

    @Override
    public Message receive(){
        return fairlossLink.receive();
    }

    @Override
    public Message deliver(Message m){
        return fairlossLink.deliver(m);
    }


}
