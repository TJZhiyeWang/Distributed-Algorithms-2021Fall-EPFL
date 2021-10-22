package cs451.Links;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StubbornLink implements Link, Runnable{

    public Queue<Record> queue;
    FairlossLink fairlossLink;
    public boolean flag = true;

    public StubbornLink(int port) {
        this.fairlossLink = new FairlossLink(port);
        this.queue = new ConcurrentLinkedQueue<>();
    }
    @Override
    public void run(){
        while(flag){
            try{
                if(!queue.isEmpty()){
                    Record record = queue.poll();
                    send(record.m, record.ipAddress, record.port);
                    queue.offer(record);
                }
                Thread.sleep(Constant.SENDINTERVAL);
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
