package client.logic;

import bootstrapper.ServerProgram;
import server.logic.IAdministration;
import shared.fontyspublisher.IRemotePropertyListener;
import shared.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class ServerClient implements IRemotePropertyListener
{
    String ipAddress = "localhost";
    private IAdministration administration;
    private IRemotePublisherForListener clientPublisher;
    private Registry registry;

    ServerClient()
    {
        try
        {
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

    public IRemotePublisherForListener getPublisher(String username)
    {
        if (clientPublisher == null)
        {
            try
            {
                clientPublisher = (IRemotePublisherForListener) registry.lookup(username);
            }
            catch (RemoteException | NotBoundException e)
            {
                e.printStackTrace();
            }
        }

        return clientPublisher;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {

    }
}
