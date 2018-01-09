package bootstrapper;

import server.logic.ServerAdministration;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerProgram
{
    private Registry registry;
    public static final String BINDING_NAME = "ServerMessenger";
    public static final int PORT_NUMBER = 2500;

    public static void main(String[] args)
    {
        new ServerProgram();
    }

    private ServerProgram()
    {
        ServerAdministration serverAdministration;

        try
        {
            serverAdministration = new ServerAdministration();
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
            registry = LocateRegistry.getRegistry(PORT_NUMBER);
            System.out.printf("Server: registry created on port number %d%n", PORT_NUMBER);
        }
        catch (RemoteException e)
        {
            System.out.println("Server: cannot create registry");
            e.printStackTrace();
        }

        try
        {
            registry.rebind(BINDING_NAME, serverAdministration);
        }
        catch (RemoteException e)
        {
            System.out.println("Server: cannot bind server administration to registry");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
