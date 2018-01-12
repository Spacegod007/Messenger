package server.logic;

import bootstrapper.ServerProgram;
import com.sun.javaws.exceptions.InvalidArgumentException;
import shared.Message;
import shared.fontyspublisher.IRemotePublisherForDomain;
import shared.fontyspublisher.RemotePublisher;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class User implements IRemotePublisherForDomain
{
    public static final String CHAT_LIST_UPDATER = "chatListUpdater";
    public static final String REGISTRY_UPDATER = "registryUpdater";
    public static final String REMOVED_PROPERTY_KEYWORD = "removed";

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
        publisher.registerProperty(CHAT_LIST_UPDATER);
        publisher.registerProperty(REGISTRY_UPDATER);
        publisher.registerProperty(REMOVED_PROPERTY_KEYWORD);

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

    boolean login(String password, long newSessionId) throws RemoteException
    {
        if (this.password.equals(password))
        {
            this.sessionId = newSessionId;
            return true;
        }

        return false;
    }

    void logout() throws RemoteException
    {
        sessionId = -1;
    }

    boolean addContact(User contact)
    {
        if (!username.equals(contact.username))
        {
            return !contacts.contains(contact) && contacts.add(contact);
        }
        else
        {
            throw new IllegalArgumentException("You can't add yourself as a contact");
        }
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
        addToChat(new Chat(this, getContactByName(contactName)));
    }

    void sendMessage(long chatId, Message message) throws RemoteException, InvalidArgumentException
    {
        getChatById(chatId).sendMessage(message);
    }

    List<Message> getChatMessages(long chatId) throws InvalidArgumentException
    {
        return getChatById(chatId).getMessages();
    }

    List<Long> getParticipatingChats()
    {
        List<Long> chatIds = new ArrayList<>();

        for (Chat chat : chats)
        {
            chatIds.add(chat.getChatId());
        }

        return chatIds;
    }

    byte[] getFile(long chatId, String filename) throws InvalidArgumentException, FileNotFoundException, RemoteException
    {
        return getChatById(chatId).getFile(filename);
    }

    void addToChat(Chat chat) throws RemoteException
    {
        chats.add(chat);

        publisher.registerProperty(String.valueOf(chat.getChatId()) + Chat.PARTICIPANTS_CHECKER);
        publisher.registerProperty(String.valueOf(chat.getChatId()) + Chat.MESSAGES_PROPERTY_NAME);

        publisher.inform(REGISTRY_UPDATER, null, chat.getChatId());
        publisher.inform(CHAT_LIST_UPDATER, null, chat.getChatName(username));
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

        throw new InvalidArgumentException(new String[]{"Contact not found"});
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

        throw new InvalidArgumentException(new String[]{"Chat not found"});
    }

    public String getChatName(long chatId) throws InvalidArgumentException
    {
        return getChatById(chatId).getChatName(username);
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
        publisher.inform(REGISTRY_UPDATER, null, REMOVED_PROPERTY_KEYWORD + property);
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
