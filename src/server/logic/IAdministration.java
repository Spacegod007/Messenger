package server.logic;

import exceptions.InvalidArgumentException;
import shared.SerializableChat;
import shared.Message;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Administration interface, used for all communication with the server
 */
public interface IAdministration extends Remote
{
    /**
     * Logs the user into the system
     * @param username of the user
     * @param password of the user
     * @return if login successful a sessionId otherwise -1
     * @throws RemoteException if something goes wrong in the connection
     */
    long login(String username, String password) throws RemoteException;

    /**
     * Registers and logs the user into the system
     * @param username of the user
     * @param password of the user
     * @return if login successful a sessionId otherwise -1
     * @throws RemoteException if something goes wrong in the connection
     */
    long register(String username, String password) throws RemoteException;

    /**
     * Logs the user out of the system
     * @param sessionId of the logged in user
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if false data was given to the server
     */
    void logout(long sessionId) throws RemoteException, InvalidArgumentException;

    /**
     * Adds a contact to the logged in user
     * @param sessionId of the logged in user
     * @param contactName of the contact to be added, must be existing user other than logged in user
     * @return true if the contact was added, false if it was not added
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if false data was given to the server
     */
    boolean addContact(long sessionId, String contactName) throws RemoteException, InvalidArgumentException;

    /**
     * Removes a contact of the logged in user
     * @param sessionId of the logged in user
     * @param contactName of the contact to be removed
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if false data was given to the server
     */
    void removeContact(long sessionId, String contactName) throws RemoteException, InvalidArgumentException;

    /**
     * Gets the contacts of the logged in user
     * @param sessionId of the logged in user
     * @return a list of strings containing all contacts of the user
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if false data was given to the server
     */
    List<String> getContacts(long sessionId) throws RemoteException, InvalidArgumentException;

    /**
     * Creates a new chat for the logged in user and the specified contact
     * @param sessionId of the logged in user
     * @param contact to be added to the chat, must already be in contactlist
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if false data was given to the server
     */
    void newChat(long sessionId, String contact) throws RemoteException, InvalidArgumentException;

    /**
     * Gets al chats the logged in user participates in
     * @param sessionId of the logged in user
     * @return a list containing all chats the user participates in
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if false data was given to the server
     */
    List<SerializableChat> getParticipatingChats(long sessionId) throws RemoteException, InvalidArgumentException;

    /**
     * Sends a message by the logged in user in the specified chat
     * @param sessionId of the logged in user
     * @param chatId of the chat to send the message to
     * @param message to be send in the chat
     * @throws RemoteException if something goes wrong in the connection
     * @throws InvalidArgumentException if false data was given to the server
     */
    void sendMessage(long sessionId, long chatId, Message message) throws RemoteException, InvalidArgumentException;
}
