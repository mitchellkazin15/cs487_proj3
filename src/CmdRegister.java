package src;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.net.*;
import java.time.*;
import java.util.concurrent.*;

public class CmdRegister extends UnicastRemoteObject implements CmdAgent {
    private static final int PORT = 42636;
    public static Beacon b;
    public char valid;
    public int time;
    public String os;

    public static void main(String[] args){
        System.loadLibrary("CMDImpl");
        String location = "127.0.0.1";
        String destination = "127.0.0.1";

        System.out.print("\nType Destination IP (Press Enter for localhost): ");
        Scanner line = new Scanner(System.in);
        String cmdLine = line.nextLine();
        Scanner sc = new Scanner(cmdLine);
        if(sc.hasNext()) {
            destination = sc.next();
        }
        System.out.print("Type the Local IP (Press Enter for localhost): ");
        line = new Scanner(System.in);
        cmdLine = line.nextLine();
        sc = new Scanner(cmdLine);
        if(sc.hasNext()) {
            location = sc.next();
        }

        Random rand = new Random();
        int id = rand.nextInt(1000000);
        int startUpTime = (int) Instant.now().getEpochSecond();
        int timeInterval = 3;
        InetAddress destIP = null;
        InetAddress localIP = null;
        try {
            localIP = InetAddress.getByName(location);
            destIP = InetAddress.getByName(location);
        } catch (Exception e){
            e.printStackTrace();
        }
        int cmdPort = PORT + rand.nextInt(200) - 100;
        b = new Beacon(id, startUpTime, timeInterval, localIP, destIP, cmdPort);
        BeaconEmitter be = new BeaconEmitter("be", b, PORT);
        be.start();
        try {
            Registry registry = LocateRegistry.createRegistry(b.cmdPort);
            CmdRegister obj = new CmdRegister();
            CmdRegister cmdService = (CmdRegister) obj;
            registry.rebind("" + b.id, cmdService );
        } catch (Exception e) { System.err.println ("Error - " + e);
        }
    }

    public CmdRegister() throws RemoteException {

    }

    @Override
    public Object execute(String CmdID, Object CmdObj) {
        this.valid = 0;
        try {
            if (CmdID.equals("GetLocalTime")) {
                getLocalTime(CmdObj);
                if(this.valid == 1){
                    Registry registry = LocateRegistry.getRegistry(b.destIP.toString().substring(1, b.destIP.toString().length()), PORT);
                    String registration = "response";
                    CmdObject stub = (CmdObject) registry.lookup(registration);
                    Integer response = new Integer(this.time);
                    stub.response(response);
                    System.out.println("Executed GetLocalTime Command");
                }
                else{
                    System.out.println("Command could not be executed");
                }
            } else if (CmdID.equals("GetLocalOS")) {
                String response = getLocalOS(CmdObj);
                if(this.valid == 1){
                    Registry registry = LocateRegistry.getRegistry(b.destIP.toString().substring(1, b.destIP.toString().length()), PORT);
                    String registration = "response";
                    CmdObject stub = (CmdObject) registry.lookup(registration);
                    stub.response(response);
                    System.out.println("Executed GetLocalOS Command");
                }
                else{
                    System.out.println("Command could not be executed");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public native void getLocalTime(Object ds);
    public native String getLocalOS(Object ds);

    private static class BeaconEmitter extends Thread {
        private Thread t;
        private String threadName;
        private Beacon b;
        private int PORT;

        public BeaconEmitter(String threadName, Beacon beacon, int port){
            this.threadName = threadName;
            this.b = beacon;
            this.PORT = port;
        }

        @Override
        public void start(){
            if (t == null) {
                t = new Thread (this, threadName);
                t.start ();
            }
        }

        @Override
        public void run(){
            try {
                Registry registry = LocateRegistry.getRegistry(b.destIP.toString().substring(1, b.destIP.toString().length()), PORT);
                String registration = "BeaconReceiver";
                BeaconReceiver stub = (BeaconReceiver) registry.lookup(registration);
                while(true){
                    stub.putBeacon(this.b);
                    Thread.sleep(b.timeInterval * 1000);
                }
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }
}
