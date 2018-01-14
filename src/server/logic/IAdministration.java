package server.logic;

import exceptions.InvalidArgumentException;
import shared.SerializableChat;
import shared.Message;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAdministration extends Remote
{
    long login(String username, String password) throws RemoteException;
    long register(String username, String password) throws RemoteException;
    void logout(long sessionId) throws RemoteException, InvalidArgumentException;

    boolean addContact(long sessionId, String contactName) throws RemoteException, InvalidArgumentException;
    void removeContact(long sessionId, String contactName) throws RemoteException, InvalidArgumentException;
    List<String> getContacts(long sessionId) throws RemoteException, InvalidArgumentException;

    void newChat(long sessionId, String contact) throws RemoteException, InvalidArgumentException;
    List<SerializableChat> getParticipatingChats(long sessionId) throws RemoteException, InvalidArgumentException;
    void sendMessage(long sessionId, long chatId, Message message) throws RemoteException, InvalidArgumentException;
    byte[] getFile(long sessionId, long chatId, String filename) throws RemoteException, InvalidArgumentException, FileNotFoundException;
}
