package cs451.Broadcast;

import cs451.Listener.Listener;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class LCBroadcast extends Listener implements Broadcast{
    URBroadcast urBroadcast;

    int next[];

    public LCBroadcast(int port){
        urBroadcast = new URBroadcast(port);

        next = new int[Constant.getHosts().size()];
        for (int i=0; i<next.length; i++){
            next[i] = 1;
        }
        Constant.initNext(next);

        this.start();
    }

    public int getSpeed(){
        return urBroadcast.bebbroadcast.perfectLink.getSpeed();
    }


    @Override
    public void broadcast(Message m){
        String log = Constant.BROADCAST + " " + m.payload + "\n";
        Constant.getLogger().log(log);
        int[] tmp = Constant.getNext().clone();
        tmp[Constant.getMyself()-1] = m.payload;
        m.initClock(tmp);
        urBroadcast.broadcast(m);
    }

    @Override
    public Record receive(){
        return urBroadcast.deliver();
    }

    @Override
    public Record deliver(){
        for (int i=0; i<Constant.getHosts().size(); i++){

            int[] tmp = (int[])Constant.getCasualRules().get(i+1);
            for (int j = 1; j < tmp.length; j++){
                while(tryDeliver(tmp[j]));
            }
            while(tryDeliver(i+1));
        }
        return null;
    }
    // try to deliver a record from process i
    private boolean tryDeliver(int i){
        Record r = urBroadcast.priorityQueues[i-1].peek();
        if (r!=null && compare(next, r.m.clock, (int[]) Constant.getCasualRules().get(i))){
            next[i-1]++;
            urBroadcast.priorityQueues[i-1].poll();
            r.m.destroyClock();
            String log = Constant.DELIVER + " " + r.m.sProcess + " " + r.m.payload + "\n";
            Constant.getLogger().log(log);
            return true;
        }
        else
            return false;
    }

    private boolean compare(int[] myClock, int[] otherClock, int[] index){
        for(int i=0; i<index.length; i++){
            if (myClock[index[i]-1] < otherClock[index[i]-1])
                return false;
        }
        return true;
    }
}
