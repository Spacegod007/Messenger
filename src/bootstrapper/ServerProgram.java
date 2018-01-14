package bootstrapper;

import server.logic.ServerAdministration;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main entry point of the server application
 */
public class ServerProgram
{
    /**
     * the registry of the server
     */
    private Registry registry;

    /**
     * the binding name to obtain the administration
     */
    public static final String BINDING_NAME = "ServerMessenger";

    /**
     * the port number used to to connect to the server
     */
    public static final int PORT_NUMBER = 2500;

    /**
     * Main entry point of the server application
     * @param args not used
     */
    public static void main(String[] args)
    {
        new ServerProgram();
    }

    /**
     * The constructor of the server application
     */
    private ServerProgram()
    {
        ServerAdministration serverAdministration;

        try
        {
            serverAdministration = new ServerAdministration(this);
            System.out.println("Server: server administration created");
        }
        catch (RemoteException e)
        {
            System.out.println("Server: cannot create server administration");
            e.printStackTrace();
            serverAdministration = null;
        }

        try
        {
            registry = LocateRegistry.createRegistry(PORT_NUMBER);
            System.out.printf("Server: registry created on port number %d%n", PORT_NUMBER);
        }
        catch (RemoteException e)
        {
            System.out.println("Server: cannot create registry");
            e.printStackTrace();
        }

        registerProperty(BINDING_NAME, serverAdministration);
    }

    /**
     * Registers a property in the registry
     * @param name of the property
     * @param remote to be bound in the registry
     */
    public void registerProperty(String name, Remote remote)
    {
        try
        {
            registry.rebind(name, remote);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
