package cs451.Links;

import cs451.Listener.Listener;
import cs451.Utils.Constant;
import cs451.Utils.Logger;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerfectLink implements Link{
    Set delivered;
    StubbornLink stubbornLink;
    Logger logger;
    Listener listener;
    Thread tstubborn;
    Thread tlistener;
    List hosts;
    public PerfectLink(int port, Logger logger, List hosts){
        this.stubbornLink = new StubbornLink(port, hosts);
        tstubborn = new Thread(this.stubbornLink);
        tstubborn.start();
        this.listener = new Listener(this, logger, hosts);
        tlistener = new Thread(this.listener);
        tlistener.start();
        this.delivered = new HashSet<Record>(4096);
        this.logger = logger;
        this.hosts = hosts;
    }

    @Override
    public void send(Message m, String ip, int port){

        String log = Constant.BROADCAST + " " + new String(Arrays.copyOf(m.payload, m.length)) + "\n";
        logger.log(log);
        try{
//            stubbornLink.send(m, ip, port);
            stubbornLink.queue.put(new Record(m, ip, port));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public Record receive(){
        return stubbornLink.receive();
    }

    @Override
    public Record deliver(Record record){
        if (delivered.contains(record)) {
            this.ack(record);
            return null;
        } else {
            delivered.add(record);
        }
        return record;
    }

    public void ack(Record record){
        Message m = new Message(record.m.payload, Constant.SEND);
        m.toACK();
//        System.out.println("send ack:" + record.i + new String(record.m.payload));
        this.stubbornLink.send(m, record.ip, record.port);
    }

    public void dequeue(Record record){
        stubbornLink.sent.add(record);
    }



    @Override
    public void close(){
        this.stubbornLink.stop();
        this.listener.stop();
//        this.stubbornLink.close();
    }
}
