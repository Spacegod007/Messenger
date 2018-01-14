package fileserver.logic;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFileStorage extends Remote
{
    /**
     * Gets a file of the file storage
     * @param filename of the file to be retrieved
     * @return a byte array containing the data of the file
     * @throws RemoteException if something goes wrong in the connection
     * @throws FileNotFoundException if the file was not found
     */
    byte[] getFile(String filename) throws RemoteException, FileNotFoundException;

    /**
     * Stores a specified file on the server
     * @param filename of the file to be stored
     * @param data of the file which should be stored
     * @throws RemoteException if something goes wrong in the connection
     */
    void storeData(String filename, byte[] data) throws RemoteException;
}
