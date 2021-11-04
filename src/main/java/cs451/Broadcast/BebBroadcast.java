package cs451.Broadcast;

import cs451.Utils.Host;
import cs451.Links.PerfectLink;
import cs451.Utils.Constant;
import cs451.Utils.Message;
import cs451.Utils.Record;

import java.util.ArrayList;
import java.util.List;

public class BebBroadcast implements Broadcast{

    PerfectLink perfectLink;
    List<Host> hosts;

    BebBroadcast(int port){
        this.perfectLink = new PerfectLink(port);
        this.hosts = new ArrayList<>(Constant.getHosts());
        //delete myself(dirty work, only used in bebbroadcast)
        int myself = Constant.getMyself();
        hosts.remove(myself - 1);
    }

    @Override
    public void broadcast(Message m){
        for (Host host:hosts){
            perfectLink.send(m, host.getIp(), host.getPort());
        }
    }

    @Override
    public Record receive(){
        return perfectLink.deliver();
    }

    @Override
    public Record deliver() {
        return this.receive();
    }
}
