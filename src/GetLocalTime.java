package src;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.time.*;
import java.util.concurrent.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

public class GetLocalTime extends UnicastRemoteObject implements CmdObject {
    public int time;
    public char valid;
    public Agent a;

    public GetLocalTime(Agent a) throws RemoteException{
        this.time = 0;
        this.valid = '0';
        this.a = a;
    }

    public void execute(){
        try {
            Registry registry = LocateRegistry.getRegistry(a.agentIP.toString().substring(1, a.agentIP.toString().length()), a.cmdPort);
            String registration = "" + a.id;
            CmdAgent stub = (CmdAgent) registry.lookup(registration);
            stub.execute("GetLocalTime", this);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void response(Object o) throws RemoteException{
        this.time = (Integer) o;
        this.valid = '1';
        this.printResponse();
        System.out.println();
    }

    public void printResponse(){
        if(this.valid == '1'){
            Date date = new Date((long)this.time*1000);
            SimpleDateFormat jdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
            System.out.println("RMI Response: " + jdf.format(date));
        }
        else{
            System.out.println("Invalid Response");
        }
    }
}
