package src;

public interface CmdAgent extends java.rmi.Remote {
    Object execute(String CmdID, Object CmdObj) throws java.rmi.RemoteException;
}
