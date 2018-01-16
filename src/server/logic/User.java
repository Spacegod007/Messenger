package server.logic;

import exceptions.InvalidArgumentException;
import shared.SerializableChat;
import shared.fontyspublisher.RemotePublisher;
import bootstrapper.ServerProgram;
import shared.Message;
import shared.fontyspublisher.IRemotePublisherForDomain;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * A user of the application
 */
public class User implements IRemotePublisherForDomain
{
    /**
     * constant used to inform the user client of changes with the chat list
     */
    public static final String CHAT_LIST_UPDATER = "chatListUpdater";

    /**
     * constant used to inform the user client of changes with the registry
     */
    public static final String REGISTRY_UPDATER = "registryUpdater";

    /**
     * constant used to inform the user client of changes with the contact list
     */
    public static final String CONTACT_LIST_UPDATER = "contactListUpdater";

    /**
     * the username of the user
     */
    private final String username;

    /**
     * the password of the user
     */
    private final String password;

    /**
     * the session id of the user
     */
    private long sessionId;

    /**
     * the contacts of the user
     */
    private final List<User> contacts;

    /**
     * the chats the user participates in
     */
    private final List<Chat> chats;

    /**
     * a publisher which informs the client-side of changes on the server
     */
    private RemotePublisher publisher;

    /**
     * The constructor of the user
     * @param username of the user
     * @param password of the user
     * @param serverProgram which is used to register the publisher
     * @throws RemoteException if something goes wrong in the creation of the publisher
     */
    public User(String username, String password, ServerProgram serverProgram) throws RemoteException
    {
        if (username == null || username.isEmpty())
        {
            throw new IllegalArgumentException("Username can't be empty");
        }

        if (password == null || password.isEmpty())
        {
            throw new IllegalArgumentException("Password can't be empty");
        }

        this.username = username;
        this.password = password;

        contacts = new ArrayList<>();
        chats = new ArrayList<>();

        publisher = new RemotePublisher();
        publisher.registerProperty(REGISTRY_UPDATER);
        publisher.registerProperty(CHAT_LIST_UPDATER);
        publisher.registerProperty(CONTACT_LIST_UPDATER);

        serverProgram.registerProperty(username, publisher);
    }

    /**
     * gets the username of the user
     * @return a string containing the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * gets the session id of the user
     * @return a long variable containing the session of the user
     */
    long getSessionId()
    {
        return sessionId;
    }

    /**
     * Gets the contacts of the user
     * @return a list of contacts
     */
    List<String> getContacts()
    {
        List<String> contactNames = new ArrayList<>();

        for (User contact : contacts)
        {
            contactNames.add(contact.username);
        }

        return contactNames;
    }

    /**
     * logs the user into the system
     * @param password of the user (must be equal to the local password variable)
     * @param newSessionId the new session id allocated if the password is correct
     * @return true if the password is correct otherwise false
     */
    boolean login(String password, long newSessionId)
    {
        if (this.password.equals(password))
        {
            this.sessionId = newSessionId;
            return true;
        }

        return false;
    }

    /**
     * logs the user out of the system
     */
    void logout()
    {
        sessionId = -1;
    }

    /**
     * adds the specified contact to the user
     * @param contact to be added (if he/she isn't already a contact)
     * @return true if the user is added as a contact otherwise false
     * @throws RemoteException if something goes wrong in informing the clients
     */
    boolean addContact(User contact) throws RemoteException
    {
        if (contact == null)
        {
            throw new IllegalArgumentException("contact cannot be null");
        }

        if (!username.equals(contact.username))
        {
            if (!contacts.contains(contact) && contacts.add(contact))
            {
                inform(CONTACT_LIST_UPDATER, null, getContacts());
                return true;
            }
        }
        else
        {
            throw new IllegalArgumentException("You can't add yourself as a contact");
        }
        return false;
    }

    /**
     * removes a contact of the user
     * @param contactName of the contact to be removed
     */
    void removeContact(String contactName)
    {
        try
        {
            User contact = getContactByName(contactName);
            contacts.remove(contact);
        }
        catch (InvalidArgumentException ignored)
        { }
    }

    /**
     * creates a new chat for the user
     * @param contactName of the contact to be invited to the chat
     * @throws RemoteException if something goes wrong in the creation of the chat
     * @throws InvalidArgumentException if the contact does not exist in the contact list
     */
    void newChat(String contactName) throws RemoteException, InvalidArgumentException
    {
        new Chat(this, getContactByName(contactName));
    }

    /**
     * sends a message in the specified chat
     * @param chatId of the chat where the message needs to get send to
     * @param message which is about to be send
     * @throws RemoteException if something goes wrong in informing all clients of the change in the chat
     * @throws InvalidArgumentException if the chat with the specified id does not exist
     */
    void sendMessage(long chatId, Message message) throws RemoteException, InvalidArgumentException
    {
        getChatById(chatId).sendMessage(message);
    }

    /**
     * Gets all chats the user participates in
     * @return a list of serializable (send able) chats
     */
    List<SerializableChat> getParticipatingChats()
    {
        List<SerializableChat> returnable = new ArrayList<>();

        for (Chat chat : chats)
        {
            returnable.add(chat.getAsSerializable());
        }

        return returnable;
    }

    /**
     * Adds the current user to the specified chat
     * @param chat to be added to
     * @throws RemoteException if something goes wrong in informing the clients of the change
     */
    void addToChat(Chat chat) throws RemoteException
    {
        chats.add(chat);

        registerProperty(chat.getChatSubscriptionName());

        inform(REGISTRY_UPDATER, null, chat.getChatSubscriptionName());

        inform(CHAT_LIST_UPDATER, null, getParticipatingChats());
    }

    /**
     * Gets the specified user by the contact name
     * @param contactName of the user
     * @return the user who own the contact name
     * @throws InvalidArgumentException if the user was not found
     */
    private User getContactByName(String contactName) throws InvalidArgumentException
    {
        for (User contact : contacts)
        {
            if (contact.getUsername().equals(contactName))
            {
                return contact;
            }
        }

        throw new InvalidArgumentException("Contact not found");
    }

    /**
     * Gets the chat by the specified id
     * @param chatId of the chat
     * @return the chat which has the specified id
     * @throws InvalidArgumentException if no chat the user participates in has the specified id
     */
    private Chat getChatById(long chatId) throws InvalidArgumentException
    {
        for (Chat chat : chats)
        {
            if (chat.getChatId() == chatId)
            {
                return chat;
            }
        }

        throw new InvalidArgumentException("Chat not found");
    }

    @Override
    public void registerProperty(String property) throws RemoteException
    {
        publisher.registerProperty(property);
        publisher.inform(REGISTRY_UPDATER, null, property);
    }

    @Override
    public void unregisterProperty(String property) throws RemoteException
    {
        publisher.unregisterProperty(property);
    }

    @Override
    public void inform(String property, Object oldValue, Object newValue) throws RemoteException
    {
        publisher.inform(property, oldValue, newValue);
    }

    @Override
    public List<String> getProperties() throws RemoteException
    {
        return publisher.getProperties();
    }
}
