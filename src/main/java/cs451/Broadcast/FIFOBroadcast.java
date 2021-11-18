package cs451.Broadcast;

import cs451.Listener.Listener;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Pair;
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
        for (int proc = 1; proc <= Constant.getHosts().size(); proc++) {
            Pair p = new Pair(proc, next[proc - 1]);
            if (urBroadcast.sharedTable.containsKey(p)) {
                next[proc - 1]++;
                Record r = urBroadcast.sharedTable.get(p);
                String log = Constant.DELIVER + " " + r.m.sProcess + " " + new String(r.m.payload) + "\n";
                Constant.getLogger().log(log);
                urBroadcast.sharedTable.remove(p);
//                return r;
            }
        }
        return null;
    }

}
