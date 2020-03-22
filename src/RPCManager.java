package src;

import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.util.concurrent.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

//This class is where execution starts for RPCManager
//It initializes threads and initializes their shared resources and Semaphores to ensure mutual exclusion
public class RPCManager {

    private static ArrayList<Agent> agents;
    private static ArrayList<AgentMonitor> monitors;
    private static Semaphore agent_sem;

    private static final int PORT = 42636;

    public static void main(String args[]){
        agents = new ArrayList<Agent>();
        monitors = new ArrayList<AgentMonitor>();
        agent_sem = new Semaphore(1);

        BeaconListener listener = new BeaconListener("listener",PORT, agents, monitors, agent_sem);
        listener.start();

        printCommands();
        while (true){
            takeCommands();
        }
    }

    //takeCommands reads user input and executes commands such as
    private static void takeCommands(){
        System.out.print("Type command: ");
        Scanner line = new Scanner(System.in);
        String cmdLine = line.nextLine();
        Scanner sc = new Scanner(cmdLine);
        if(!sc.hasNext()) return;
        String cmd = sc.next();
        cmd = cmd.toLowerCase().trim();
        if(cmd.equals("ls")){
            listAgents();
        }
        else if(cmd.equals("h")){
            printCommands();
        }
        else if(cmd.equals("time")){
            if(sc.hasNextInt()){
                int agentNum = sc.nextInt();
                if(agentNum < agents.size()){
                    Agent a = agents.get(agentNum);
                    if(a.alive){
                        System.out.println();
                        try {
                            Registry registry = LocateRegistry.getRegistry(PORT);
                            GetLocalTime time = new GetLocalTime(a);
                            CmdObject cmdResponse = (CmdObject) time;
                            registry.rebind("response", cmdResponse);
                            time.execute();
                        } catch (Exception e){

                        }
                    }
                    else{
                        System.out.println("\nAgent is no longer connected\n");
                    }
                }
                else {
                    System.out.println("\nNot a valid Agent Number\n");
                }
            }
            else{
                System.out.println("\nPlease provide an agent number\n");
            }
        }
        else if(cmd.equals("os")){
            if(sc.hasNextInt()){
                int agentNum = sc.nextInt();
                if(agentNum < agents.size()){
                    Agent a = agents.get(agentNum);
                    if(a.alive){
                        System.out.println();
                        try {
                            Registry registry = LocateRegistry.getRegistry(PORT);
                            GetLocalOS os = new GetLocalOS(a);
                            CmdObject cmdResponse = (CmdObject) os;
                            registry.rebind("response", cmdResponse);
                            os.execute();
                        } catch (Exception e){

                        }
                    }
                    else{
                        System.out.println("\nAgent is no longer connected\n");
                    }
                }
                else {
                    System.out.println("\nNot a valid Agent Number\n");
                }
            }
            else{
                System.out.println("\nPlease provide an agent number\n");
            }
        }
        else {
            System.out.println("\n" + cmd + " is not a recognized command\n");
        }
    }

    private static void printCommands(){
        System.out.println("\nType 'h' to list commands");
        System.out.println("Type 'ls' to list agents");
        System.out.println("Type 'time' followed by the Agent number (not ID)");
        System.out.println("    to get local time of agent");
        System.out.println("Type 'os' followed by the Agent number (not ID)");
        System.out.println("    to get local operating system of agent\n");
    }

    private static void listAgents(){
        try {
            agent_sem.acquire();
            int size = agents.size();
            if(size == 0){
                System.out.println("\nNo agents have been detected\n");
            }
            else{
                System.out.println();
            }
            for (int i = 0; i < size; ++i) {
                if(agents.get(i).alive){
                    System.out.println("Agent " + i + ": Alive");
                }
                else{
                    System.out.println("Agent " + i + ": Dead");
                }
                agents.get(i).printAgent();
                System.out.println();
            }
            agent_sem.release();
        }
        catch (InterruptedException e){

        }
    }

    private static int getMonitor(Agent agent){
        for(int i = 0; i < monitors.size(); ++i){
            if(monitors.get(i).agent.equals(agent)){
                return i;
            }
        }
        return -1;
    }
}


