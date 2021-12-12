package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.HashSet;
import java.util.Set;

public class PerfectLink implements Link{
    Set delivered;
    StubbornLink stubbornLink;
    Thread tstubborn;


    public PerfectLink(int port){
        this.stubbornLink = new StubbornLink(port);
        tstubborn = new Thread(this.stubbornLink);
        tstubborn.start();
        this.delivered = new HashSet<Record>();
    }

    public int getSpeed(){
        return stubbornLink.queue.size();
    }

    @Override
    public void send(Message m, String ip, int port){
        try{
            stubbornLink.queue.put(new Record(m, Constant.getProcessIdFromIpAndPort(ip, port)));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public Record receive(){
        return stubbornLink.deliver();
    }

    @Override
    public Record deliver(){
        Record record = this.receive();
        if (record.m.flag == Constant.ACK){
            dequeue(record);
            return null;
        }else{
            if (delivered.contains(record)) {
                this.ack(record);
                return null;
            } else {
                Message m  = new Message(record.m.payload, record.m.sProcess);
                Record r = new Record(m, record.i);
                delivered.add(r);
            }
            return record;
        }
    }

    public void ack(Record record){
        Message m = new Message(record.m.payload, record.m.sProcess);
        m.revert();
        m.destroyClock();
        this.stubbornLink.send(m, Constant.getIpFromHosts(record.i), Constant.getPortFromHosts(record.i));
    }

    public void dequeue(Record record){
        stubbornLink.sent.add(record);
    }



    @Override
    public void close(){
        this.stubbornLink.stop();
    }
}
