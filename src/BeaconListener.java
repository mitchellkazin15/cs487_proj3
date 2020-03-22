package src;

import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.util.concurrent.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

//This class establishes UDP connection with clients
public class BeaconListener extends Thread{
    private Thread t;
    private String threadName;
    private int port;
    private ArrayList<Agent> agents;
    private ArrayList<AgentMonitor> monitors;
    private Semaphore agent_sem;

    public BeaconListener(String threadName, int port, ArrayList<Agent> agents, ArrayList<AgentMonitor> monitors, Semaphore agent_sem){
        this.threadName = threadName;
        this.port = port;
        this.agents = agents;
        this.monitors = monitors;
        this.agent_sem = agent_sem;
    }

    @Override
    public void start(){
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    //When thread is started this function connects to a socket and recieves UDP packets that it sends to DataProcessor
    @Override
    public void run(){
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            BeaconReceiverImpl obj = new BeaconReceiverImpl();
            BeaconReceiver receiverService = (BeaconReceiver) obj;
            registry.rebind("BeaconReceiver", receiverService );
        } catch (Exception e) { System.err.println ("Error - " + e);
        }
    }

    private class BeaconReceiverImpl extends UnicastRemoteObject implements BeaconReceiver {

        public BeaconReceiverImpl() throws RemoteException {
        }

        @Override
        public void putBeacon(Beacon b) throws RemoteException {
            if(b != null) {
                DataProcessor dp = new DataProcessor("dp", b);
                dp.start();
            }
        }
    }

    //This is a private class that processes data from BeaconListener
    //A new thread is created every time a beacon is recieved
    //If beacon is from a new client, an Agent Monitor thread is created
    private class DataProcessor extends Thread {
        private Thread t;
        private String threadName;
        private byte[] data;
        private Beacon beacon;

        public DataProcessor(String threadName, byte[] data){
            this.threadName = threadName;
            this.data = data;
            reverse(this.data);
        }

        public DataProcessor(String threadName, Beacon beacon){
            this.threadName = threadName;
            this.beacon = beacon;
            reverse(this.data);
        }

        @Override
        public void run(){
            try {
                AgentMonitor monitor = null;

                agent_sem.acquire();

                Agent agent = new Agent(this.beacon);
                Agent oldAgent = newAgent(agent);
                if(oldAgent == null){
                    agents.add(agent);
                    monitor = new AgentMonitor("m", port, agent, agents, agent_sem);
                    monitors.add(monitor);
                }
                else{
                    oldAgent.lastBeacon = Instant.now().getEpochSecond();
                }

                agent_sem.release();

                if(monitor != null){
                    monitor.start();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        //checks if beacon is from a new agent or not
        private Agent newAgent(Agent a){
            Agent temp;
            for(int i = 0; i < agents.size(); ++i){
                temp = agents.get(i);
                if(a.equals(temp)){
                    return temp;
                }
            }
            return null;
        }

        @Override
        public void start(){
            if (t == null) {
                t = new Thread (this, threadName);
                t.start ();
            }
        }

        //converts byte array to int
        private int byteToInt(byte[] bytes){
            return   bytes[3] & 0xFF |
                    (bytes[2] & 0xFF) << 8 |
                    (bytes[1] & 0xFF) << 16 |
                    (bytes[0] & 0xFF) << 24;
        }


        //reverses a byte array
        private void reverse(byte[] array) {
            if (array == null) {
                return;
            }
            int i = 0;
            int j = array.length - 1;
            byte tmp;
            while (j > i) {
                tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
                i++;
            }
        }


    }
}
