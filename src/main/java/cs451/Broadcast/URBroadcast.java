package cs451.Broadcast;

import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class URBroadcast implements Broadcast, Runnable{
    BebBroadcast bebbroadcast;
    HashSet<Message> delivered;
    HashMap<Message, HashSet<Integer>> pending;
    public LinkedBlockingQueue<Record> sharedQueue;
    int processNum;


    URBroadcast(int port){
        bebbroadcast = new BebBroadcast(port);
        delivered = new HashSet<>();
        pending = new HashMap<>();
        processNum = Constant.getHosts().size();
        sharedQueue = new LinkedBlockingQueue<>();


        new Thread(this).start();
    }

    @Override
    public void run(){
        while(true){
            this.deliver();
        }
    }

    @Override
    public void broadcast(Message m){
        pending.put(m,new HashSet<>(){{add(Constant.getMyself());}});
        bebbroadcast.broadcast(m);
    }

    @Override
    public Record receive(){
        return bebbroadcast.deliver();
    }

    @Override
    public Record deliver(){
        Record record = this.receive();
        if (record != null){
            if (delivered.contains(record.m))//already been delivered
                return null;
            if (pending.containsKey(record.m)){
                HashSet s = pending.get(record.m);
                s.add(record.i);
                if (s.size() > processNum/2){
                    delivered.add(record.m);
                    pending.remove(record.m);
                    try {
                        sharedQueue.put(record);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    return record;
                }
            }else{
                pending.put(record.m, new HashSet<>(){{add(record.i);}});
                this.broadcast(record.m);
            }
        }
        return null;
    }

}

