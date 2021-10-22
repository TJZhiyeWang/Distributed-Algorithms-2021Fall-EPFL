package cs451.Links;

import cs451.Host;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StubbornLink implements Link, Runnable{

    public Queue<Record> queue;
    FairlossLink fairlossLink;
    public boolean flag = true;
    List hosts;

    public StubbornLink(int port, List hosts) {
        this.fairlossLink = new FairlossLink(port, hosts);
        this.queue = new ConcurrentLinkedQueue<>();
        this.hosts = hosts;
    }
    @Override
    public void run(){
        while(flag){
            try{
                if(!queue.isEmpty()){
                    for (int i=0; i<queue.size(); i++) {
                        Record record = queue.poll();
                        String ip = Constant.getIpFromHosts(hosts, record.i);
                        int port = Constant.getPortFromHosts(hosts, record.i);
                        send(record.m, ip, port);
                        queue.offer(record);
                    }
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
