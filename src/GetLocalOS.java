package src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

public class GetLocalOS extends UnicastRemoteObject implements CmdObject {
    String os;
    char valid;
    Agent a;

    public GetLocalOS(Agent a) throws RemoteException{
        this.os = "";
        this.valid = '0';
        this.a = a;
    }

    public void execute(){
        try {
            Registry registry = LocateRegistry.getRegistry(a.agentIP.toString().substring(1, a.agentIP.toString().length()), a.cmdPort);
            String registration = "" + a.id;
            CmdAgent stub = (CmdAgent) registry.lookup(registration);
            stub.execute("GetLocalOS", this);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void response(Object o) throws RemoteException{
        this.os = (String) o;
        this.valid = '1';
        this.printResponse();
        System.out.println();
    }

    public void printResponse(){
        if(this.valid == '1'){
            System.out.println("RMI Response: " + this.os);
        }
        else{
            System.out.println("Invalid Response");
        }
    }
}
