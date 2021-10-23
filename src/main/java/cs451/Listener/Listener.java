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
            Record tmp = perfectLink.receive();
            if (tmp.m.flag == Constant.SEND){
                Record record = perfectLink.deliver(tmp);
                if (record != null) {
                    System.out.println("receive packet:" + tmp.i + " " + new String(tmp.m.payload));
                    String log = Constant.DELIVER + " " + record.i + " " + new String(record.m.payload) + "\n";
                    logger.log(log);
                }
            }else if(tmp.m.flag == Constant.ACK) {
//                System.out.println("receive ack:" + tmp.i + new String(tmp.m.payload));
                perfectLink.dequeue(tmp);
            }
        }
    }

    public void stop(){
        this.flag = false;
    }
}
