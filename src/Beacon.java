package src;

import java.net.*;

public class Beacon implements java.io.Serializable {
    public int id;
    public int startUpTime;
    public int timeInterval;
    public InetAddress localIP;
    public InetAddress destIP;
    public int cmdPort;

    public Beacon(int id, int startUpTime, int timeInterval, InetAddress localIP, InetAddress destIP, int cmdPort){
        this.id = id;
        this.startUpTime = startUpTime;
        this.timeInterval = timeInterval;
        this.localIP = localIP;
        this.destIP = destIP;
        this.cmdPort = cmdPort;
    }
}
