package client.logic;

import bootstrapper.ServerProgram;
import server.logic.IAdministration;
import shared.fontyspublisher.IRemotePublisherForListener;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class ServerClient
{
    /**
     * ip address of the server
     */
    private final String ipAddress = "localhost";
    /**
     * The administration object which was received from the server
     */
    private IAdministration administration;
    /**
     * The publisher of the logged in client
     */
    private IRemotePublisherForListener clientPublisher;
    /**
     * The registry which is located on the server
     */
    private Registry registry;

    /**
     * The constructor of the connection initiator class
     */
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

    /**
     * Gets the administration object of the client
     * @return the administration object
     */
    public IAdministration getAdministration()
    {
        return administration;
    }

    /**
     * Gets the publisher object of the server
     * @param username of the logged in user
     * @return a publisher object
     */
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
}
