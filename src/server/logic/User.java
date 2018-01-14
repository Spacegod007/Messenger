package server.logic;

import exceptions.InvalidArgumentException;
import shared.SerializableChat;
import shared.fontyspublisher.RemotePublisher;
import bootstrapper.ServerProgram;
import shared.Message;
import shared.fontyspublisher.IRemotePublisherForDomain;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class User implements IRemotePublisherForDomain
{
    public static final String CHAT_LIST_UPDATER = "chatListUpdater";
    public static final String REGISTRY_UPDATER = "registryUpdater";
    public static final String CONTACT_LIST_UPDATER = "contactListUpdater";

    private final String username;
    private final String password;
    private long sessionId;

    private final List<User> contacts;
    private final List<Chat> chats;

    private RemotePublisher publisher;

    User(String username, String password, ServerProgram serverProgram) throws RemoteException
    {
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

    public String getUsername()
    {
        return username;
    }

    long getSessionId()
    {
        return sessionId;
    }

    List<String> getContacts()
    {
        List<String> contactNames = new ArrayList<>();

        for (User contact : contacts)
        {
            contactNames.add(contact.username);
        }

        return contactNames;
    }

    boolean login(String password, long newSessionId)
    {
        if (this.password.equals(password))
        {
            this.sessionId = newSessionId;
            return true;
        }

        return false;
    }

    void logout()
    {
        sessionId = -1;
    }

    boolean addContact(User contact) throws RemoteException
    {
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

    void newChat(String contactName) throws RemoteException, InvalidArgumentException
    {
        new Chat(this, getContactByName(contactName));
    }

    void sendMessage(long chatId, Message message) throws RemoteException, InvalidArgumentException
    {
        getChatById(chatId).sendMessage(message);
    }

    List<SerializableChat> getParticipatingChats()
    {
        List<SerializableChat> returnable = new ArrayList<>();

        for (Chat chat : chats)
        {
            returnable.add(chat.getAsSerializable());
        }

        return returnable;
    }

    byte[] getFile(long chatId, String filename) throws InvalidArgumentException, FileNotFoundException, RemoteException
    {
        return getChatById(chatId).getFile(filename);
    }

    void addToChat(Chat chat) throws RemoteException
    {
        chats.add(chat);

        registerProperty(chat.getChatSubscriptionName());

        inform(REGISTRY_UPDATER, null, chat.getChatSubscriptionName());

        inform(CHAT_LIST_UPDATER, null, getParticipatingChats());
    }

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
