package fileserver.logic;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFileStorage extends Remote
{
    byte[] getFile(String filename) throws RemoteException, FileNotFoundException;
    void storeData(String filename, byte[] data) throws RemoteException;
}
