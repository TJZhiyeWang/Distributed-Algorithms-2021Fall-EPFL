package cs451.Broadcast;

import cs451.Listener.Listener;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class LCBroadcast extends Listener implements Broadcast{
    URBroadcast urBroadcast;
    PriorityBlockingQueue<Record>[] priorityQueues;

    int next[];

    public LCBroadcast(int port){
        urBroadcast = new URBroadcast(port);

        priorityQueues = new PriorityBlockingQueue[Constant.getHosts().size()];
        for (int i=0; i<Constant.getHosts().size(); i++){
            priorityQueues[i] = new PriorityBlockingQueue<Record>(1024, new Comparator<Record>(){
                public int compare(Record o1, Record o2) {
                    return o1.m.clock[Constant.getMyself()-1] - o2.m.clock[Constant.getMyself()-1];
                }
            });
        }

        next = new int[Constant.getHosts().size()];
        for (int i=0; i<next.length; i++){
            next[i] = 1;
        }

        this.start();
    }


    @Override
    public void broadcast(Message m){
        String log = Constant.BROADCAST + " " + m.payload + "\n";
        System.out.println(log);
        Constant.getLogger().log(log);
        m.initClock(next);
        urBroadcast.broadcast(m);
    }

    @Override
    public Record receive(){
        return urBroadcast.deliver();
    }

    @Override
    public Record deliver(){
        if (urBroadcast.sharedQueue.isEmpty()){
            for (int i=0; i<Constant.getHosts().size(); i++){
                Record r = priorityQueues[i].peek();
                if (r!=null && compare(next, r.m.clock, (int[]) Constant.getCasualRules().get(r.m.sProcess))){
                    String log = Constant.DELIVER + " " + r.m.sProcess + " " + r.m.payload + "\n";
                    Constant.getLogger().log(log);
                    next[i]++;
                    priorityQueues[i].poll();
                }
            }
        }else{
            Record r = urBroadcast.sharedQueue.poll();
            if (r!=null && compare(next, r.m.clock, (int[]) Constant.getCasualRules().get(r.m.sProcess))){
                String log = Constant.DELIVER + " " + r.m.sProcess + " " + r.m.payload + "\n";
                Constant.getLogger().log(log);
                next[r.m.sProcess-1]++;
            }else{
                priorityQueues[r.m.sProcess-1].put(r);
            }
        }
        return null;
    }

    private boolean compare(int[] myClock, int[] otherClock, int[] index){
        if (myClock[index[0]-1] < otherClock[index[0]-1])
            return false;
        for(int i=1; i<index.length; i++){
            if (myClock[index[i]-1] <= otherClock[index[i]-1])
                return false;
        }
        return true;
    }
}
