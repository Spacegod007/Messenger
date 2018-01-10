package bootstrapper;

import fileserver.logic.FileStorage;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FileServerProgram
{
    private Registry registry;
    public static final String BINDING_NAME = "FileServer";
    public static final int PORT_NUMBER = 2501;

    public static void main(String[] args)
    {
        new FileServerProgram();
    }

    private FileServerProgram()
    {
        FileStorage fileStorage;

        try
        {
            fileStorage = new FileStorage();
            System.out.println("FileServer: file storage created");
        }
        catch (RemoteException e)
        {
            System.out.println("FileServer: cannot create file storage");
            e.printStackTrace();
            fileStorage = null;
        }

        try
        {
            registry = LocateRegistry.createRegistry(PORT_NUMBER);
            System.out.printf("FileServer: registry created on port number %d%n", PORT_NUMBER);
        }
        catch (RemoteException e)
        {
            System.out.println("FileServer: cannot create registry");
            e.printStackTrace();
        }

        try
        {
            registry.rebind(BINDING_NAME, fileStorage);
        }
        catch (RemoteException e)
        {
            System.out.println("FileServer: cannot bind file storage to registry");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
