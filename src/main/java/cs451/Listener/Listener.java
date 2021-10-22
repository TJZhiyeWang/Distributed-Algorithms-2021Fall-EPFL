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
    List<Host> hosts;
    public boolean flag = true;

    public Listener(PerfectLink perfectLink, Logger logger, List hosts){
        this.perfectLink = perfectLink;
        this.logger = logger;
        this.hosts = hosts;
    }

    @Override
    public void run(){
        while(flag){
//            try {
                Record record = perfectLink.deliver(perfectLink.receive());
                if (record != null) {
                    int srcProcess = Constant.getProcessIdFromIpAndPort(hosts, record.ipAddress, record.port);
                    String log = Constant.DELIVER + " " + srcProcess + " " + record.m.payload + "\n";
                    logger.log(log);
                    System.out.println(log);
                }
//                Thread.sleep(Constant.RECEIVEINTERVAL);
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
        }
    }

    public void stop(){
        this.flag = false;
    }
}
