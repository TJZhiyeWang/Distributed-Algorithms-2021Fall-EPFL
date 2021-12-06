package cs451;

import cs451.Broadcast.LCBroadcast;
import cs451.Parser.Parser;
import cs451.Utils.Constant;
import cs451.Utils.Host;
import cs451.Utils.Logger;
import cs451.Utils.Message;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {


    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        //write/flush output file if necessary
        System.out.println("Writing output.");

        Constant.getLogger().close();
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
            int messageNum = Integer.parseInt(scan.nextLine());//how many messages each process should send
            //build casual rules
            HashMap<Integer, int[]> casualRule = new HashMap<>();
            while (scan.hasNextLine()){
                String line = scan.nextLine();
                String[] numbers = line.split(" ");
                int[] affectProcess = new int[numbers.length];
                for (int i=0; i<affectProcess.length; i++){
                    affectProcess[i] = Integer.parseInt(numbers[i]);
                }
                casualRule.put(Integer.parseInt(numbers[0]), affectProcess);
            }
            Constant.initCasualRules(casualRule);
            //init myself
            Host host = parser.hosts().get(parser.myId()-1);
            Constant.initMyself(parser.myId());
            //init logger
            Logger logger = new Logger(parser.output());
            Constant.initlogger(logger);
            //init host
            Constant.initHost(parser.hosts());
            LCBroadcast lcBroadcast = new LCBroadcast(host.getPort());

            System.out.println("Broadcasting and delivering messages...\n");

            for (int j = 1; j <= messageNum; j++){
                //build message
                Message m = new Message(j, parser.myId());
                lcBroadcast.broadcast(m);
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
