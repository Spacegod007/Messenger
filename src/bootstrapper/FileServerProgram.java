package bootstrapper;

import fileserver.logic.FileStorage;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main entry point of the file server application
 */
public class FileServerProgram
{
    /**
     * Registry located on the files erver
     */
    private Registry registry;

    /**
     * The binding name of to obtain the file storage object
     */
    public static final String BINDING_NAME = "FileServer";

    /**
     * port used to connect to the file storage
     */
    public static final int PORT_NUMBER = 2501;

    /**
     * main entry point of the file server program
     * @param args not used
     */
    public static void main(String[] args)
    {
        new FileServerProgram();
    }

    /**
     * the constructor of the file server
     */
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
