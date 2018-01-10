package server.logic;

import com.sun.javaws.exceptions.InvalidArgumentException;
import shared.fontyspublisher.IRemotePropertyListener;
import shared.fontyspublisher.RemotePublisher;
import shared.Message;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class User
{
    private static final String CHAT_LIST_UPDATER = "chatListUpdater";

    private final String username;
    private final String password;
    private long sessionId;

    private final List<User> contacts;
    private final List<Chat> chats;

    private RemotePublisher publisher;
    private IRemotePropertyListener listener;

    User(String username, String password) throws RemoteException
    {
        this.username = username;
        this.password = password;

        contacts = new ArrayList<>();
        chats = new ArrayList<>();

        publisher = new RemotePublisher();
        publisher.registerProperty(CHAT_LIST_UPDATER);
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

    public IRemotePropertyListener getListener()
    {
        return listener;
    }

    boolean login(String password, long newSessionId, IRemotePropertyListener listener) throws RemoteException
    {
        if (this.password.equals(password))
        {
            this.listener = listener;
            this.sessionId = newSessionId;

            publisher.subscribeRemoteListener(listener, CHAT_LIST_UPDATER);

            for (Chat chat : chats)
            {
                chat.subscribeRemoteListener(listener, Chat.MESSAGES_PROPERTY_NAME);
            }

            return true;
        }

        return false;
    }

    void logout() throws RemoteException
    {
        sessionId = -1;

        for (Chat chat : chats)
        {
            chat.unsubscribeRemoteListener(listener, Chat.MESSAGES_PROPERTY_NAME);
        }
    }

    boolean addContact(User contact)
    {
        return !contacts.contains(contact) && contacts.add(contact);
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

        publisher.inform(CHAT_LIST_UPDATER, null, chat.toString());
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
}
