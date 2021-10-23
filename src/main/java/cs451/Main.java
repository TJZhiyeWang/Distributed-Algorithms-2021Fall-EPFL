package cs451;

import cs451.Links.PerfectLink;
import cs451.Utils.Constant;
import cs451.Utils.Logger;
import cs451.Utils.Message;

import java.io.*;
import java.util.Scanner;

public class Main {

    static PerfectLink perfectLink;
    static Logger logger;

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        perfectLink.close();
        //write/flush output file if necessary
        System.out.println("Writing output.");

        logger.close();
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID: " + pid + "\n");
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

        System.out.println("My ID: " + parser.myId() + "\n");
        System.out.println("List of resolved hosts is:");
        System.out.println("==========================");
        for (Host host: parser.hosts()) {
            System.out.println(host.getId());
            System.out.println("Human-readable IP: " + host.getIp());
            System.out.println("Human-readable Port: " + host.getPort());
            System.out.println();
        }
        System.out.println();

        System.out.println("Path to output:");
        System.out.println("===============");
        System.out.println(parser.output() + "\n");

        System.out.println("Path to config:");
        System.out.println("===============");
        System.out.println(parser.config() + "\n");

        System.out.println("Doing some initialization\n");
        try{
            Scanner scan = new Scanner(new FileReader(parser.config()));
            int messageNum = Integer.parseInt(scan.next());//how many messages each process should send
            int destinationProcess = Integer.parseInt(scan.next());//process should receive the messages
            Host host = parser.hosts().get(parser.myId()-1);
            logger = new Logger(parser.output());
            perfectLink = new PerfectLink(host.getPort(), logger, parser.hosts());
            System.out.println("Broadcasting and delivering messages...\n");
            if (parser.myId() != destinationProcess){
                for (Integer j = 1; j <= messageNum; j++){
                    //build message
                    Message m = new Message(j.toString().getBytes(), Constant.SEND);
                    perfectLink.send(m, Constant.getIpFromHosts(parser.hosts(), destinationProcess),Constant.getPortFromHosts(parser.hosts(), destinationProcess));
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

//         After a process finishes broadcasting,
//         it waits forever for the delivery of messages.
        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }
    }
}
