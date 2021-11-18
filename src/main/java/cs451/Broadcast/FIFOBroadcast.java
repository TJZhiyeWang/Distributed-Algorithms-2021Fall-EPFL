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
    public Record deliver(){
        try {
            for (int i = 0; i < urBroadcast.shareQueue.size(); i++) {
                Message m = urBroadcast.shareQueue.take();
                int proc = m.sProcess;
                int lsn = m.seq;
                if (next[proc - 1] == lsn) {
                    next[proc - 1]++;
                    return new Record(m, m.sProcess);
                }
                urBroadcast.shareQueue.put(m);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
