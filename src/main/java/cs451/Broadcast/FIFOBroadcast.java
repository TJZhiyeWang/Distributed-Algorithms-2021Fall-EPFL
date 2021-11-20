package cs451.Broadcast;

import cs451.Listener.Listener;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class FIFOBroadcast extends Listener implements Broadcast{

    URBroadcast urBroadcast;
    PriorityBlockingQueue<Record>[] priorityQueues;

    int[] next;

    public FIFOBroadcast(int port){
        urBroadcast = new URBroadcast(port);
        next = new int[Constant.getHosts().size()];
        for (int i=0; i<next.length; i++){
            next[i] = 1;
        }
        priorityQueues = new PriorityBlockingQueue[Constant.getHosts().size()];
        for (int i=0; i<Constant.getHosts().size();i++){
            priorityQueues[i] = new PriorityBlockingQueue<Record>(1024, new Comparator<Record>(){
                public int compare(Record o1, Record o2) {
                    return o1.m.seq - o2.m.seq;
                }
            });
        }
        this.start();
    }

    @Override
    public void broadcast(Message m){
        String log = Constant.BROADCAST + " " + new String(m.payload) + "\n";
        Constant.getLogger().log(log);
        urBroadcast.broadcast(m);
    }

    @Override
    public Record receive(){
        return urBroadcast.deliver();
    }

    @Override
    public Record deliver() {
        //build a message and find whether the hashset has such kind of message
        if (urBroadcast.sharedQueue.isEmpty()){
            for (int i=0; i<Constant.getHosts().size(); i++){
                Record record = priorityQueues[i].peek();
                if (record!=null && record.m.seq == next[i]){
                    String log = Constant.DELIVER + " " + record.m.sProcess + " " + new String(record.m.payload) + "\n";
                    Constant.getLogger().log(log);
                    next[i]++;
                    priorityQueues[i].poll();
                }
            }
        }else{
            Record r = urBroadcast.sharedQueue.poll();
            if (r.m.seq == next[r.m.sProcess-1]){
                String log = Constant.DELIVER + " " + r.m.sProcess + " " + new String(r.m.payload) + "\n";
                Constant.getLogger().log(log);
                next[r.m.sProcess-1]++;
            }else{
                priorityQueues[r.m.sProcess-1].put(r);
            }
        }
        return null;
    }

}
