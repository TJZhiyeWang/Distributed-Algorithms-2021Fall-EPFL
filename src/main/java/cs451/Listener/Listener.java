package cs451.Listener;

import cs451.Links.PerfectLink;
import cs451.Utils.Constant;
import cs451.Utils.Logger;
import cs451.Utils.Message;
import cs451.Utils.Record;

public class Listener {
    PerfectLink perfectLink;
    Logger logger;
    Listener(PerfectLink perfectLink, Logger logger){
        this.perfectLink = perfectLink;
        this.logger = logger;
        Runnable listener = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Message m = perfectLink.deliver(perfectLink.receive());
                        if (m != null) {
                            String record = Constant.DELIVER + " " + m.srcProcess + " " + m.payload + "\n";
                            logger.write(record);
                        }
                        Thread.sleep(Constant.INTERVAL);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t = new Thread(listener);
        t.run();
    }
}
