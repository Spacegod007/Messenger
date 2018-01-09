package client.logic;

import bootstrapper.ServerProgram;
import server.logic.IAdministration;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class ServerClient
{
    private IAdministration administration;
    private Registry registry;

    ServerClient()
    {
        try
        {
            String ipAddress = "localhost";

            registry = LocateRegistry.getRegistry(ipAddress, ServerProgram.PORT_NUMBER);
        }
        catch (RemoteException e)
        {
            System.out.println("Client.ServerClient: cannot locate registry");
            e.printStackTrace();
        }

        if (registry != null)
        {
            try
            {
                administration = (IAdministration) registry.lookup(ServerProgram.BINDING_NAME);
            } catch (NotBoundException | RemoteException e)
            {
                System.out.println("Client.ServerClient: cannot bind administration");
                e.printStackTrace();
            }
        }
    }

    public IAdministration getAdministration()
    {
        return administration;
    }
}
