package client.logic;

import exceptions.InvalidArgumentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import server.logic.IAdministration;
import server.logic.User;
import shared.ChatMessage;
import shared.SerializableChat;
import shared.fontyspublisher.IRemotePropertyListener;
import shared.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Administration extends UnicastRemoteObject implements IRemotePropertyListener
{
    private final IAdministration administration;
    private IRemotePublisherForListener publisher;

    private final ServerClient serverClient;

    private long sessionId;
    private String username;
    private final Object serialiser;

    private final ObservableList<String> observableContacts;
    private final ObservableList<String> observableParticipatingChatNames;

    private ObservableList<SerializableChat> participatingChats;

    private ObservableMap<String, SerializableChat> chatByName;

    public Administration() throws RemoteException
    {
        super();

        serverClient = new ServerClient();
        chatByName = FXCollections.observableHashMap();

        serialiser = new Object();

        participatingChats = FXCollections.observableArrayList();
        observableParticipatingChatNames = FXCollections.observableArrayList();
        observableContacts = FXCollections.observableArrayList();

        administration = serverClient.getAdministration();
    }

    public ObservableList<String> getParticipatingChatNames()
    {
        return observableParticipatingChatNames;
    }

    public String getUsername()
    {
        return username;
    }

    public ObservableList<String> getContacts()
    {
        return observableContacts;
    }

    public ObservableMap<String, SerializableChat> getChatByName()
    {
        return chatByName;
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
            return administration.addContact(sessionId, contactName);
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

    public void sendFile(byte[] file, String filename)
    {

    }

    public void sendMessage(String chatName, String message)
    {
        long chatId = chatByName.get(chatName).getChatId();
        ChatMessage chatMessage = new ChatMessage(message, username);

        try
        {
            administration.sendMessage(sessionId, chatId, chatMessage);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public byte[] getFile(String chatName, String filename)
    {
        return null;
    }

    private void chatMessagePropertyNameChanged(PropertyChangeEvent evt)
    {

    }

    private void getUserData() throws RemoteException, InvalidArgumentException
    {
//        participatingChatIds = administration.getParticipatingChats(sessionId);
        getAllChatData(administration.getParticipatingChats(sessionId));
        observableContacts.addAll(administration.getContacts(sessionId));

        subscribeAllProperties();
    }

    private void getAllChatData(List<SerializableChat> participatingChats)
    {
        this.participatingChats.clear();
        this.participatingChats.addAll(participatingChats);

        chatByName.clear();
        observableParticipatingChatNames.clear();

        for (SerializableChat chat : participatingChats)
        {
            String chatName = chat.getName(username);
            observableParticipatingChatNames.add(chatName);
            chatByName.put(chatName, chat);
        }
    }

    public SerializableChat getChatByName(String chatName)
    {
        return chatByName.get(chatName);
    }

    private void subscribeAllProperties() throws RemoteException
    {
        subscribeProperty(User.REGISTRY_UPDATER);
        subscribeProperty(User.CONTACT_LIST_UPDATER);
        subscribeProperty(User.CHAT_LIST_UPDATER);

        for (SerializableChat chat : participatingChats)
        {
            subscribeProperty(chat.getChatSubscriptionName());
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
                registryUpdaterChanged(evt);
                return;
            case User.CONTACT_LIST_UPDATER:
                contactListUpdaterChanged(evt);
                return;
            case User.CHAT_LIST_UPDATER:
                userChatListUpdaterChanged(evt);
                return;
            default:
                break;
        }

        for (SerializableChat chat : participatingChats)
        {
            if (evt.getPropertyName().equals(chat.getChatSubscriptionName()))
            {
                chatMessagesChanged(chat, evt);
                return;
            }
        }
    }

    private void chatMessagesChanged(SerializableChat chat, PropertyChangeEvent evt)
    {
        SerializableChat newValue = (SerializableChat) evt.getNewValue();

        for (int i = 0; i < participatingChats.size(); i++)
        {
            if (chat.getChatId() == participatingChats.get(i).getChatId())
            {
                participatingChats.set(i, newValue);
                chatByName.replace(chat.getName(username), newValue);
            }
        }
    }

    private void userChatListUpdaterChanged(PropertyChangeEvent evt)
    {
        observableParticipatingChatNames.clear();
        getAllChatData((List<SerializableChat>) evt.getNewValue());
    }

    private void contactListUpdaterChanged(PropertyChangeEvent evt)
    {
        observableContacts.clear();
        observableContacts.addAll((List<String>) evt.getNewValue());
    }
    
    private void registryUpdaterChanged(PropertyChangeEvent evt)
    {
        try
        {
            publisher.subscribeRemoteListener(this, (String) evt.getNewValue());
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
