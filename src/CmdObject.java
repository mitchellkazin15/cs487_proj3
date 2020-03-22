package src;

public interface CmdObject extends java.rmi.Remote {
    void response(Object o) throws java.rmi.RemoteException;
}