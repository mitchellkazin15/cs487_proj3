package src;

import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.util.concurrent.*;

//This class stores information about individual agents
public class Agent {
    public int id;
    public int startUpTime;
    public int timeInterval;
    public InetAddress agentIP;
    public InetAddress destIP;
    public int cmdPort;
    public long lastBeacon;
    public boolean alive;

    public Agent(Beacon b){
        this.id = b.id;
        this.startUpTime = b.startUpTime;
        this.timeInterval = b.timeInterval;
        this.agentIP = b.localIP;
        this.destIP = b.destIP;
        this.cmdPort = b.cmdPort;
        this.lastBeacon = Instant.now().getEpochSecond();
        this.alive = true;
    }

    public void printAgent(){
        System.out.println("Agent ID is: " + id);
        System.out.println("Start time is: " + startUpTime);
        System.out.println("Source IP Address is: " + agentIP.toString());
        System.out.println("Dest. IP Address is: " + agentIP.toString());
        System.out.println("Time interval is: " + timeInterval);
        System.out.println("Command Port is: " + cmdPort);
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }

        Agent a = (Agent)o;

        if(this.id == a.id){
            if(this.startUpTime == a.startUpTime){
                return true;
            }
        }
        return false;
    }
}
