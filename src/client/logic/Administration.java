package client.logic;

import exceptions.InvalidArgumentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import server.logic.IAdministration;
import server.logic.User;
import shared.ChatMessage;
import shared.FileMessage;
import shared.SerializableChat;
import shared.fontyspublisher.IRemotePropertyListener;
import shared.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Main administration class for the client application
 */
public class Administration extends UnicastRemoteObject implements IRemotePropertyListener
{
    /**
     * Main communication entry point for client-initiated methods
     */
    private final IAdministration administration;

    /**
     * Publisher which calls methods from the server onto the client
     */
    private IRemotePublisherForListener publisher;

    /**
     * The client-server connector class
     */
    private final ServerClient serverClient;

    /**
     * SessionId of the client
     */
    private long sessionId;

    /**
     * The username of the currently logged in client
     */
    private String username;

    /**
     * An observable list of all contacts of the currently logged in client
     */
    private final ObservableList<String> observableContacts;

    /**
     * An observable list of all chat names this client participates in
     */
    private final ObservableList<String> observableParticipatingChatNames;

    /**
     * An observable list of all chats this client participates in
     */
    private ObservableList<SerializableChat> participatingChats;

    /**
     * An observable mapping which links the name of a chat to a chat
     */
    private ObservableMap<String, SerializableChat> chatByName;

    /**
     * Constructor of the administration class
     * @throws RemoteException if something goes wrong in the connection to the server
     */
    public Administration() throws RemoteException
    {
        super();

        serverClient = new ServerClient();
        chatByName = FXCollections.observableHashMap();

        participatingChats = FXCollections.observableArrayList();
        observableParticipatingChatNames = FXCollections.observableArrayList();
        observableContacts = FXCollections.observableArrayList();

        administration = serverClient.getAdministration();
    }

    /**
     * Gets the observable list of all participating chat names
     * @return the observable list instance
     */
    public ObservableList<String> getParticipatingChatNames()
    {
        return observableParticipatingChatNames;
    }

    /**
     * Gets the contacts of the user
     * @return an observable list containing all the contacts as strings
     */
    public ObservableList<String> getContacts()
    {
        return observableContacts;
    }

    /**
     * Gets the observable mapping of all chats by name
     * @return the observable mapping instance
     */
    public ObservableMap<String, SerializableChat> getChatByName()
    {
        return chatByName;
    }

    /**
     * Logs the user into the system
     * @param username of the user
     * @param password of the user
     * @return true if login succeeds, false if it fails
     */
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

    /**
     * Registers and logs the user into the system
     * @param username of the user
     * @param password of the user
     * @return true if the registration succeeded, false if it failed
     * @throws InvalidArgumentException if the username was already in use
     */
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

    /**
     * Logs the user out of the system
     */
    public void logout()
    {
        try
        {
            administration.logout(sessionId);
            publisher.unsubscribeRemoteListener(this, null);
        }
        catch (RemoteException | InvalidArgumentException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Adds a contact to the user
     * @param contactName of the contact
     * @return true if the contact was added, false if it failed adding the contact
     * @throws RemoteException if something goes wrong in the connection
     */
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

    /**
     * Removes a contact of the user
     * @param contactName of the contact
     */
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

    /**
     * Creates a new chat with the specified contact
     * @param contact which will also be added to the chat
     */
    public void newChat(String contact)
    {
        try
        {
            administration.newChat(sessionId, contact);
        }
        catch (RemoteException | InvalidArgumentException ignored)
        { }
    }

    /**
     * Sends a file to the server
     * @param chatName of the chat
     * @param file to be send
     */
    public void sendFile(String chatName, File file)
    {
        try
        {
            final byte[] data = Files.readAllBytes(file.toPath());
            long chatId = chatByName.get(chatName).getChatId();
            administration.sendMessage(sessionId, chatId, new FileMessage(data, file.getName(), username));
        }
        catch (InvalidArgumentException | IOException ignored)
        { }
    }

    /**
     * Sends a message to the specified chat
     * @param chatName of the chat
     * @param message which will be send to the chat
     */
    public void sendMessage(String chatName, String message)
    {
        long chatId = chatByName.get(chatName).getChatId();
        ChatMessage chatMessage = new ChatMessage(message, username);

        try
        {
            administration.sendMessage(sessionId, chatId, chatMessage);
        }
        catch (RemoteException | InvalidArgumentException ignored)
        { }
    }

    /**
     * Gets all initial user data when the user logs in to the system
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if the specified data could not be obtained due to false information being given
     */
    private void getUserData() throws RemoteException, InvalidArgumentException
    {
        getAllChatData(administration.getParticipatingChats(sessionId));
        observableContacts.addAll(administration.getContacts(sessionId));

        subscribeAllProperties();
    }

    /**
     * Gets all chat data out of the list of chats
     * @param participatingChats the user participates in
     */
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

    /**
     * Gets a chat by the specified name
     * @param chatName of the chat
     * @return a chat object
     */
    public SerializableChat getChatByName(String chatName)
    {
        return chatByName.get(chatName);
    }

    /**
     * Subscribes the user to the initial properties it needs to be subscribed to
     * @throws RemoteException if something goes wrong in the connection
     */
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

    /**
     * Subscribes the user to the specified property
     * @param property to be subscribed to
     * @throws RemoteException if something goes wrong in the connection
     */
    private void subscribeProperty(String property) throws RemoteException
    {
        publisher.subscribeRemoteListener(this, property);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
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

    /**
     * Is being called when the chat messages object changed on the server
     * @param chat which messages got changed
     * @param evt event which is called on a property change
     */
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

    /**
     * Is being called when the chat list of chats the user participates in has changed on the server
     * @param evt event which is called on a property change
     */
    private void userChatListUpdaterChanged(PropertyChangeEvent evt)
    {
        observableParticipatingChatNames.clear();
        getAllChatData((List<SerializableChat>) evt.getNewValue());
    }

    /**
     * Is being called when the contact list of the user has changed on the server
     * @param evt event which is called on a property change
     */
    private void contactListUpdaterChanged(PropertyChangeEvent evt)
    {
        observableContacts.clear();
        observableContacts.addAll((List<String>) evt.getNewValue());
    }

    /**
     * Is being called when the client is required to subscribe to another property
     * @param evt event which is called on a property change
     */
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

    /**
     * Gets the send file out of a message to save it in the specified location
     * @param fileMessage which contains the file data
     * @param file location where it needs to be saved
     * @throws IOException if something goes wrong in writing the data
     */
    public void getFile(FileMessage fileMessage, File file) throws IOException
    {
        Files.write(file.toPath(), fileMessage.getContents());
    }
}
