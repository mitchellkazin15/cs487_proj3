package src;

public interface BeaconReceiver extends java.rmi.Remote {
    void putBeacon(Beacon b) throws java.rmi.RemoteException;
}