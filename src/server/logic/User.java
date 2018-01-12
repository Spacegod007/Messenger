package server.logic;

import exceptions.InvalidArgumentException;
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

    public User(String username, String password) throws RemoteException
    {
        this.username = username;
        this.password = password;

        contacts = new ArrayList<>();
        chats = new ArrayList<>();

        publisher = new RemotePublisher();
    }

    public String getUsername()
    {
        return username;
    }

    public long getSessionId()
    {
        return sessionId;
    }

    public List<String> getContacts()
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

    public boolean login(String password, long newSessionId, IRemotePropertyListener listener) throws RemoteException
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

    public void logout() throws RemoteException
    {
        sessionId = -1;

        for (Chat chat : chats)
        {
            chat.unsubscribeRemoteListener(listener, Chat.MESSAGES_PROPERTY_NAME);
        }
    }

    public boolean addContact(User contact)
    {
        return !contacts.contains(contact) && contacts.add(contact);
    }

    public void removeContact(String contactName)
    {
        try
        {
            User contact = getContactByName(contactName);
            contacts.remove(contact);
        }
        catch (InvalidArgumentException ignored)
        { }
    }

    public void newChat(String contactName) throws RemoteException, InvalidArgumentException
    {
        addToChat(new Chat(this, getContactByName(contactName)));
    }

    public void sendMessage(long chatId, Message message) throws RemoteException, InvalidArgumentException
    {
        getChatById(chatId).sendMessage(message);
    }

    public List<Message> getChatMessages(long chatId) throws InvalidArgumentException
    {
        return getChatById(chatId).getMessages();
    }

    public List<Long> getParticipatingChats()
    {
        List<Long> chatIds = new ArrayList<>();

        for (Chat chat : chats)
        {
            chatIds.add(chat.getChatId());
        }

        return chatIds;
    }

    public byte[] getFile(long chatId, String filename) throws InvalidArgumentException, FileNotFoundException, RemoteException
    {
        return getChatById(chatId).getFile(filename);
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

    public void addToChat(Chat chat) throws RemoteException
    {
        chats.add(chat);

        publisher.inform(CHAT_LIST_UPDATER, null, chat.toString());
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
}
