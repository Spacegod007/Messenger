package server.logic;

import bootstrapper.FileServerProgram;
import fileserver.logic.IFileStorage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class FileServerClient
{
    private IFileStorage fileStorage;
    private Registry registry;

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

    public IFileStorage getFileStorage()
    {
        return fileStorage;
    }
}
