package server.logic;

import bootstrapper.FileServerProgram;
import fileserver.logic.IFileStorage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Server to fileserver connector class
 */
class FileServerClient
{
    /**
     * The filestorage object where objects can be stored
     */
    private IFileStorage fileStorage;

    /**
     * The registry located on the filestorage
     */
    private Registry registry;

    /**
     * Constructor of the connector class
     */
    FileServerClient()
    {
        try
        {
            String ipAddress = "localhost";

            registry = LocateRegistry.getRegistry(ipAddress, FileServerProgram.PORT_NUMBER);
        }
        catch (RemoteException e)
        {
            System.out.println("Server.FileServerClient: cannot locate registry");
            e.printStackTrace();
        }

        if (registry != null)
        {
            try
            {
                fileStorage = (IFileStorage) registry.lookup(FileServerProgram.BINDING_NAME);
            } catch (NotBoundException | RemoteException e)
            {
                System.out.println("Server.FileServerClient: cannot bind file storage");
                e.printStackTrace();
            }
        }
    }

    /**
     * gets the filestorage object
     * @return a filestorage object
     */
    public IFileStorage getFileStorage()
    {
        return fileStorage;
    }
}
