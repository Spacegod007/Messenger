package server.logic;

import bootstrapper.ServerProgram;
import exceptions.InvalidArgumentException;
import shared.Message;
import shared.SerializableChat;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ServerAdministration extends UnicastRemoteObject implements IAdministration
{
    private final ServerProgram serverProgram;
    private AtomicLong nextSessionId;
    private List<User> users;

    private final Object synchronizer;

    public ServerAdministration(ServerProgram serverProgram) throws RemoteException
    {
        super();

        this.serverProgram = serverProgram;

        this.users = new ArrayList<>();
        nextSessionId = new AtomicLong(1);

        synchronizer = new Object();
    }

    @Override
    public long login(String username, String password) throws RemoteException
    {
        if (username == null || username.isEmpty())
        {
            throw new IllegalArgumentException("Username can't be null");
        }

        if (password == null || password.isEmpty())
        {
            throw new IllegalArgumentException("Password can't be null");
        }

        long returnable = -1;

        synchronized (synchronizer)
        {
            try
            {
                while (getUserBySessionId(nextSessionId.get()) != null)
                {
                    nextSessionId.incrementAndGet();
                }
            }
            catch (InvalidArgumentException ignored)
            { }

            try
            {
                if (getUserByUsername(username).login(password, nextSessionId.get()))
                {
                    returnable = nextSessionId.getAndIncrement();
                }
            }
            catch (InvalidArgumentException e)
            {
                return -1;
            }
        }
        checkMaxSessionId();

        return returnable;
    }

    private void checkMaxSessionId()
    {
        if (nextSessionId.get() == Long.MAX_VALUE - 1)
        {
            nextSessionId.set(1);
        }
    }

    @Override
    public long register(String username, String password) throws RemoteException
    {
        if (username == null || username.isEmpty())
        {
            throw new IllegalArgumentException("Username can't be null");
        }

        if (password == null || password.isEmpty())
        {
            throw new IllegalArgumentException("Password can't be null");
        }

        synchronized (synchronizer)
        {
            if (!isExistingUser(username))
            {
                users.add(new User(username, password, serverProgram));
            }
            else
            {
                return -1;
            }
        }

        return login(username, password);
    }

    @Override
    public void logout(long sessionId) throws RemoteException, InvalidArgumentException
    {
        getUserBySessionId(sessionId).logout();
    }

    @Override
    public boolean addContact(long sessionId, String contactName) throws InvalidArgumentException, RemoteException
    {
        return isExistingUser(contactName) && getUserBySessionId(sessionId).addContact(getUserByUsername(contactName));
    }

    @Override
    public void removeContact(long sessionId, String contactName) throws InvalidArgumentException
    {
        getUserBySessionId(sessionId).removeContact(contactName);
    }

    @Override
    public void newChat(long sessionId, String contact) throws RemoteException, InvalidArgumentException
    {
        getUserBySessionId(sessionId).newChat(contact);
    }

    @Override
    public void sendMessage(long sessionId, long chatId, Message message) throws InvalidArgumentException, RemoteException
    {
        getUserBySessionId(sessionId).sendMessage(chatId, message);
    }

    @Override
    public List<SerializableChat> getParticipatingChats(long sessionId) throws InvalidArgumentException
    {
        return getUserBySessionId(sessionId).getParticipatingChats();
    }

    @Override
    public List<String> getContacts(long sessionId) throws InvalidArgumentException
    {
        return getUserBySessionId(sessionId).getContacts();
    }

    @Override
    public byte[] getFile(long sessionId, long chatId, String filename) throws InvalidArgumentException, FileNotFoundException, RemoteException
    {
        return getUserBySessionId(sessionId).getFile(chatId, filename);
    }

    public boolean isExistingUser(String username)
    {
        try
        {
            getUserByUsername(username);
            return true;
        }
        catch (InvalidArgumentException e)
        {
            return false;
        }
    }

    private User getUserBySessionId(long sessionId) throws InvalidArgumentException
    {
        if (sessionId == -1 || sessionId == 0)
        {
            throw new IllegalArgumentException("Session id not in use");
        }

        synchronized (synchronizer)
        {
            for (User user : users)
            {
                if (user.getSessionId() == sessionId)
                {
                    return user;
                }
            }
        }

        throw new InvalidArgumentException("User session not found");
    }

    private User getUserByUsername(String username) throws InvalidArgumentException
    {
        synchronized (synchronizer)
        {
            for (User user : users)
            {
                if (user.getUsername().equals(username))
                {
                    return user;
                }
            }
        }

        throw new InvalidArgumentException(String.format("User by the name of %s not found", username));
    }
}
