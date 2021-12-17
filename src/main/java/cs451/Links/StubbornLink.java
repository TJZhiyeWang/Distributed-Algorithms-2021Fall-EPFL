package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

public class StubbornLink implements Link, Runnable{

    public LinkedBlockingQueue<Record> queue;
    FairlossLink fairlossLink;
    public boolean flag = true;
    public HashSet<Record> sent;

    public StubbornLink(int port) {
        this.fairlossLink = new FairlossLink(port);
        this.queue = new LinkedBlockingQueue<Record>();
        this.sent = new HashSet<>();
    }
    @Override
    public void run(){
        while(flag){
//            int num = this.queue.size();
//            if (num > 000)
            try{
                Record record = this.queue.take();
                System.out.println("queue size: " + this.queue.size());
                System.out.println("Set size: " + this.sent.size());
                if (sent.contains(record)) {
                    sent.remove(record);
                    continue;
                }
                send(record.m, Constant.getIpFromHosts(record.i), Constant.getPortFromHosts(record.i));
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
        return fairlossLink.deliver();
    }

    @Override
    public Record deliver(){
        return this.receive();
    }

    @Override
    public void close(){ fairlossLink.close(); }

    public void stop(){
        this.flag = false;
    }


}
