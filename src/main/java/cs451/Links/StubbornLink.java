package cs451.Links;

import cs451.Host;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class StubbornLink implements Link, Runnable{

    public LinkedBlockingQueue<Record> queue;
    FairlossLink fairlossLink;
    public boolean flag = true;
    List hosts;

    public StubbornLink(int port, List hosts) {
        this.fairlossLink = new FairlossLink(port, hosts);
        this.queue = new LinkedBlockingQueue<Record>();
        this.hosts = hosts;
    }
    @Override
    public void run(){
        while(flag){
            try{
                int j = 0;
                do {
                    Record record = this.queue.take();
                    String ip = Constant.getIpFromHosts(hosts, record.i);
                    int port = Constant.getPortFromHosts(hosts, record.i);
                    send(record.m, ip, port);
                    queue.put(record);
                } while(j < queue.size());
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
