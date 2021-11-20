package cs451.Broadcast;

import cs451.Listener.Listener;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

public class FIFOBroadcast extends Listener implements Broadcast{

    URBroadcast urBroadcast;
    int[] next;

    public FIFOBroadcast(int port){
        urBroadcast = new URBroadcast(port);
        next = new int[Constant.getHosts().size()];
        for (int i=0; i<next.length; i++){
            next[i] = 1;
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
        for (int i=0; i<Constant.getHosts().size(); i++){
            Record record = urBroadcast.priorityQueues[i].peek();
            if (record!=null && record.m.seq == next[i]){
                String log = Constant.DELIVER + " " + record.m.sProcess + " " + new String(record.m.payload) + "\n";
                Constant.getLogger().log(log);
                next[i]++;
                try {
                    urBroadcast.priorityQueues[i].take();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
