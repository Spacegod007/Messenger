package client.logic;

import com.sun.javaws.exceptions.InvalidArgumentException;
import server.logic.Chat;
import server.logic.IAdministration;
import server.logic.User;
import shared.Message;
import shared.fontyspublisher.IRemotePropertyListener;
import shared.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Administration extends UnicastRemoteObject implements IRemotePropertyListener
{
    private final IAdministration administration;
    private IRemotePublisherForListener publisher;

    private final ServerClient serverClient;

    private long sessionId;
    private String username;
    private final Object serialiser;

    private List<Long> participatingChatIds;
    private List<String> participatingChatNames;
    private Map<String, Long> participatingChats;
    private Map<Long, List<Message>> participatingChatMessages;

    private List<String> contacts;

    public Administration() throws RemoteException
    {
        super();

        serverClient = new ServerClient();
        participatingChatIds = new ArrayList<>();
        participatingChatNames = new ArrayList<>();
        participatingChats = new HashMap<>();
        contacts = new ArrayList<>();
        participatingChatMessages = new HashMap<>();
        serialiser = new Object();

        administration = serverClient.getAdministration();
    }

    public List<String> getParticipatingChatNames()
    {
        return participatingChatNames;
    }

    public String getUsername()
    {
        return username;
    }

    public List<String> getContacts()
    {
        return contacts;
    }

    public List<Message> getChatMessages(String chatName)
    {
        return participatingChatMessages.get(getChatIdByName(chatName));
    }

    public boolean login(String username, String password)
    {
        try
        {
            sessionId = administration.login(username, password);
            if (sessionId != -1)
            {
                this.username = username;
                publisher = serverClient.getPublisher(username);

                this.getUserData();
                return true;
            }
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(String username, String password) throws InvalidArgumentException
    {
        try
        {
            sessionId = administration.register(username, password);
            if (sessionId != -1)
            {
                this.username = username;
                publisher = serverClient.getPublisher(username);

                this.getUserData();
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

    public boolean addContact(String contactName) throws RemoteException
    {
        try
        {
            if (administration.addContact(sessionId, contactName))
            {
                contacts.add(contactName);
                return true;
            }
        }
        catch (InvalidArgumentException ignored)
        { }

        return false;
    }

    public void removeContact(String contactName)
    {
        try
        {
            administration.removeContact(sessionId, contactName);
            contacts.remove(contactName);
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


    public void sendMessage(String chatName, Message message)
    {
        try
        {
            administration.sendMessage(sessionId, getChatIdByName(chatName), message);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public byte[] getFile(String chatName, String filename)
    {
        try
        {
            return administration.getFile(sessionId, getChatIdByName(chatName), filename);
        }
        catch (RemoteException | FileNotFoundException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private void chatMessagePropertyNameChanged(PropertyChangeEvent evt)
    {
        long chatId = (long) evt.getNewValue();
        try
        {
            participatingChatMessages.replace(chatId, administration.getChatMessages(sessionId, chatId));
        }
        catch (InvalidArgumentException | RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private void userChatListUpdaterChanged(PropertyChangeEvent evt)
    {
        try
        {
            List<Long> chatIds = (List<Long>) evt.getNewValue();
            getAllChatData(chatIds);
        }
        catch (InvalidArgumentException | RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private void getUserData() throws RemoteException, InvalidArgumentException
    {
        participatingChatIds = administration.getParticipatingChats(sessionId);
        getAllChatData(participatingChatIds);
        subscribeAllProperties(participatingChatIds);
        contacts = administration.getContacts(sessionId);
    }

    private void getAllChatData(List<Long> chatIds) throws RemoteException, InvalidArgumentException
    {
        participatingChats.clear(); //mapping of key:String by value:Long (chatName, chatId)
        participatingChatNames.clear();
        participatingChatMessages.clear();

        for (Long chatId : chatIds)
        {
            String chatName = administration.getChatName(sessionId, chatId);
            participatingChatNames.add(chatName);
            participatingChats.put(chatName, chatId);
            participatingChatMessages.put(chatId, administration.getChatMessages(sessionId, chatId));
        }
    }

    private long getChatIdByName(String chatName)
    {
        return participatingChats.get(chatName);
    }

    private void subscribeAllProperties(List<Long> participatingChatIds) throws RemoteException
    {
        subscribeProperty(User.REGISTRY_UPDATER);
        subscribeProperty(User.CHAT_LIST_UPDATER);
        subscribeProperty(User.REMOVED_PROPERTY_KEYWORD);

        for (Long chatId : participatingChatIds)
        {
            subscribeProperty(chatId + Chat.MESSAGES_PROPERTY_NAME);
            subscribeProperty(chatId + Chat.PARTICIPANTS_CHECKER);
        }
    }

    private void subscribeProperty(String property) throws RemoteException
    {
        publisher.subscribeRemoteListener(this, property);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        switch (evt.getPropertyName())
        {
            case User.REGISTRY_UPDATER:
                return;
            case User.REMOVED_PROPERTY_KEYWORD:
                return;
            case User.CHAT_LIST_UPDATER:
                userChatListUpdaterChanged(evt);
                return;
            default:
                break;
        }

        for (Long chatId : participatingChatIds)
        {
            if (evt.getPropertyName().equals(chatId + Chat.MESSAGES_PROPERTY_NAME))
            {

                return;
            }
            if (evt.getPropertyName().equals(chatId + Chat.PARTICIPANTS_CHECKER))
            {

                return;
            }
        }

        try
        {
            throw new Exception(String.format("You forgot to subscribe to %s idiot", evt.getPropertyName()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
