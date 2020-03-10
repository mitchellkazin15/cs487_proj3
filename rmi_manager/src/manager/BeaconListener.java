package manager;

import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.util.concurrent.*;

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
        byte buffer[] = new byte[1024];
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(port);

        }
        catch(SocketException e){
            System.out.println("Could not open Socket on port: " + this.port);
            System.out.println(e.getMessage());
            return;
        }

        while(true){
            try {
                ds.receive(incoming);
            }
            catch (IOException e){
                continue;
            }
            byte[] data = new byte[incoming.getLength()];
            System.arraycopy(incoming.getData(), 0, data, 0, data.length);
            DataProcessor dp = new DataProcessor("dp", data);
            dp.start();
        }
    }

    //This is a private class that processes data from BeaconListener
    //A new thread is created every time a beacon is recieved
    //If beacon is from a new client, an Agent Monitor thread is created
    private class DataProcessor extends Thread {
        private Thread t;
        private String threadName;
        private byte[] data;

        public DataProcessor(String threadName, byte[] data){
            this.threadName = threadName;
            this.data = data;
            reverse(this.data);
        }

        @Override
        public void run(){
            if(data.length != 24){
                System.out.println(data.length);
                return;
            }
            byte[] bID = new byte[4];
            System.arraycopy(data,16,bID,0,4);
            int id = byteToInt(bID);

            System.arraycopy(data,12,bID,0,4);
            int startUpTime = byteToInt(bID);

            System.arraycopy(data,8,bID,0,4);
            InetAddress agentIP = null;
            try {
                reverse(bID);
                agentIP = InetAddress.getByAddress(bID);
            }
            catch(UnknownHostException e){
                System.out.println("Could not resolve address");
            }

            System.arraycopy(data,8,bID,0,4);
            InetAddress destIP = null;
            try {
                reverse(bID);
                destIP = InetAddress.getByAddress(bID);
            }
            catch(UnknownHostException e){
                System.out.println("Could not resolve address");
            }

            System.arraycopy(data,4,bID,0,4);
            int timeInterval = byteToInt(bID);

            System.arraycopy(data,0,bID,0,4);
            int cmdPort = byteToInt(bID);

            try {
                AgentMonitor monitor = null;

                agent_sem.acquire();

                Agent agent = new Agent(id, startUpTime, timeInterval, agentIP, destIP, cmdPort);
                Agent oldAgent = newAgent(agent);
                if(oldAgent == null){
                    agent.ss = new ServerSocket(agent.cmdPort);
                    agent.socket = agent.ss.accept();
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
            }
            catch (InterruptedException | IOException e){
                System.out.println(e.getMessage());
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
