package manager;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.util.concurrent.*;

//this class establishes a TCP connection with its assigned agent and recieves OS and time information
public class AgentMonitor extends Thread {
    private Thread t;
    private String threadName;
    private int port;
    private ArrayList<Agent> agents;
    private Semaphore agent_sem;
    public Agent agent;

    public AgentMonitor(String threadName, int port, Agent agent, ArrayList<Agent> agents, Semaphore agent_sem){
        this.agent = agent;
        this.threadName = threadName;
        this.port = port;
        this.agents = agents;
        this.agent_sem = agent_sem;
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
            while (true) {
                Thread.sleep(agent.timeInterval * 1000);

                agent_sem.acquire();

                if( Instant.now().getEpochSecond() - agent.lastBeacon > 2 * agent.timeInterval){
                    agent.alive = false;
                    try {
                        agent.socket.close();
                    }
                    catch (IOException e){

                    }
                    agent_sem.release();
                    break;
                }

                agent_sem.release();
            }

        }
        catch (InterruptedException e){

        }
    }

}
