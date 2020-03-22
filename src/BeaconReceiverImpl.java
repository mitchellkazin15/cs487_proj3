package src;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.ArrayList;

public class BeaconReceiverImpl extends UnicastRemoteObject implements BeaconReceiver {
    public ArrayList<Beacon> beacons;

    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.createRegistry(42636);
            BeaconReceiverImpl obj = new BeaconReceiverImpl();
            BeaconReceiver receiverService = (BeaconReceiver) obj;
            registry.rebind("BeaconReceiver", receiverService );
        } catch (Exception e) { System.err.println ("Error - " + e);
        }
    }

    public BeaconReceiverImpl() throws RemoteException {
        beacons = new ArrayList<>();
    }

    @Override
    public void putBeacon(Beacon b) throws RemoteException {
        System.out.println("received!");
    }
}