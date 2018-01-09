package client.logic;

import com.sun.javaws.exceptions.InvalidArgumentException;
import server.logic.IAdministration;
import shared.Message;
import shared.fontyspublisher.IRemotePropertyListener;

import java.beans.PropertyChangeEvent;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.List;

public class Administration implements IRemotePropertyListener
{
    private final IAdministration administration;
    private long sessionId;

    public Administration()
    {
        ServerClient serverClient = new ServerClient();
        administration = serverClient.getAdministration();
    }

    public boolean login(String username, String password)
    {
        try
        {
            sessionId = administration.login(username, password, this);
            if (sessionId != -1)
            {
                return true;
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(String username, String password)
    {
        try
        {
            sessionId = administration.register(username, password, this);
            if (sessionId != -1)
            {
                return true;
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void logout()
    {
        try
        {
            administration.logout(sessionId);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public boolean addContact(String contactName)
    {
        try
        {
            return administration.addContact(sessionId, contactName);
        }
        catch (InvalidArgumentException | RemoteException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void removeContact(String contactName)
    {
        try
        {
            administration.removeContact(sessionId, contactName);
        }
        catch (InvalidArgumentException | RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public void newChat(String contact)
    {
        try
        {
            administration.newChat(sessionId, contact);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(long chatId, Message message)
    {
        try
        {
            administration.sendMessage(sessionId, chatId, message);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public List<Message> getChatMessages(long chatId)
    {
        try
        {
            return administration.getChatMessages(sessionId, chatId);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<Long> getParticipatingChats()
    {
        try
        {
            return administration.getParticipatingChats(sessionId);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getContacts()
    {
        try
        {
            return administration.getContacts(sessionId);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getFile(long chatId, String filename)
    {
        try
        {
            return administration.getFile(sessionId, chatId, filename);
        }
        catch (RemoteException | FileNotFoundException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {

    }
}
