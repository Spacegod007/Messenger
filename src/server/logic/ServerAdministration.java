package server.logic;

import bootstrapper.ServerProgram;
import exceptions.InvalidArgumentException;
import shared.Message;
import shared.SerializableChat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Main communication entry point of the server application
 */
public class ServerAdministration extends UnicastRemoteObject implements IAdministration
{
    /**
     * The class which created this class, passes connection data
     */
    private final ServerProgram serverProgram;

    /**
     * The next available session id
     */
    private AtomicLong nextSessionId;

    /**
     * ALl users that are currently registered on the server
     */
    private List<User> users;

    /**
     * A synchronizer object to prevent data from being accessed at the same time in multiple locations
     */
    private final Object synchronizer;

    /**
     * The constructor of the server administration
     * @param serverProgram which initiated this class
     * @throws RemoteException if something goes wrong in setting up the connections
     */
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

    /**
     * Checks if the maximum session id is closing in
     */
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

    /**
     * Checks if an user by the specified name exists
     * @param username of the user
     * @return true if the user exists, false if it does not exist
     */
    private boolean isExistingUser(String username)
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

    /**
     * Gets a specified user object by session id
     * @param sessionId of the user
     * @return the user which is linked to this session id
     * @throws InvalidArgumentException if user session was not found
     */
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

    /**
     * Gets an user object by username
     * @param username of the user
     * @return the user who owns this username
     * @throws InvalidArgumentException if no user has the specified username
     */
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
