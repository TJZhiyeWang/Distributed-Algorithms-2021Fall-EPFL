package cs451.Listener;
import cs451.Broadcast.Broadcast;
import cs451.Utils.Constant;
import cs451.Utils.Record;

// I am responsible for delivering and printing log :)
public abstract class Listener extends Thread implements Broadcast {
    public boolean flag = true;

    @Override
    public void run(){
        while(flag){
            Record record = deliver();
//            if (record != null) {
////                String log = Constant.DELIVER + " " + record.m.sProcess + " " + new String(record.m.payload) + "\n";
////                Constant.getLogger().log(log);
//            }
        }
    }

    public void close(){
        this.flag = false;
    }
}
