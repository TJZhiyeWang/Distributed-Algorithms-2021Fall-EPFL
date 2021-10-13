package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.HashSet;
import java.util.Set;

public class StubbornLink implements Link{

    Set sent;
    FairlossLink fairlossLink;
    StubbornLink(int port) {
        this.fairlossLink = new FairlossLink(port);
        this.sent = new HashSet<Record>();
        Runnable timer = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        for(Object obj: sent){
                            Record record = (Record) obj;
                            send(record.m, record.ipAddress, record.port);
                        }
                        Thread.sleep(Constant.INTERVAL);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t = new Thread(timer);
        t.run();
    }

    @Override
    synchronized public void send(Message m, String ip, int port){
        fairlossLink.send(m, ip, port);
        Record record = new Record(m, ip, port);
        if (!sent.contains(record))
            sent.add(record);
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
