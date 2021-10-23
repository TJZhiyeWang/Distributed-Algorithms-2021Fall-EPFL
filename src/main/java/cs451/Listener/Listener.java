package cs451.Listener;

import cs451.Host;
import cs451.Links.PerfectLink;
import cs451.Utils.Constant;
import cs451.Utils.Logger;
import cs451.Utils.Record;

import java.util.List;

public class Listener implements Runnable {
    PerfectLink perfectLink;
    Logger logger;
    public boolean flag = true;

    public Listener(PerfectLink perfectLink, Logger logger){
        this.perfectLink = perfectLink;
        this.logger = logger;
    }

    @Override
    public void run(){
        while(flag){
            Record record = perfectLink.deliver(perfectLink.receive());
            if (record != null) {
                if (record.m.flag == Constant.SEND){
                    String log = Constant.DELIVER + " " + record.i + " " + new String(record.m.payload) + "\n";
                    logger.log(log);
                }else if(record.m.flag == Constant.ACK){
                    perfectLink.dequeue(record);
                }
            }
        }
    }

    public void stop(){
        this.flag = false;
    }
}
