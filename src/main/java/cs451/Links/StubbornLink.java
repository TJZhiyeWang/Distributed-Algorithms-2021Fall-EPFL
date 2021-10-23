package cs451.Links;

import cs451.Host;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

public class StubbornLink implements Link, Runnable{

    public LinkedBlockingQueue<Record> queue;
    FairlossLink fairlossLink;
    public boolean flag = true;
    List hosts;
    public HashSet<Record> sent;

    public StubbornLink(int port, List hosts) {
        this.fairlossLink = new FairlossLink(port, hosts);
        this.queue = new LinkedBlockingQueue<Record>();
        this.hosts = hosts;
        this.sent = new HashSet<>();
    }
    @Override
    public void run(){
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(Constant.SENDINTERVAL);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        while(flag){
            try{
                Record record = this.queue.take();
                if (queue.size() == 0) {
                    long end = System.currentTimeMillis();
                    System.out.println("execute time: " + (end - start) / 1000);
                }
//                System.out.println("queue size: " + queue.size());
//                System.out.println("set size: " + sent.size());
                if (sent.contains(record)){
                    sent.remove(record);
                    continue;
                }
                send(record.m, record.ip, record.port);
                queue.put(record);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void send(Message m, String ip, int port){
        fairlossLink.send(m, ip, port);
    }

    @Override
    public Record receive(){
        return fairlossLink.receive();
    }

    @Override
    public Record deliver(Record m){
        return fairlossLink.deliver(m);
    }

    @Override
    public void close(){ fairlossLink.close(); }

    public void stop(){
        this.flag = false;
    }


}
