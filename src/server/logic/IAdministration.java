package server.logic;

import com.sun.javaws.exceptions.InvalidArgumentException;
import shared.fontyspublisher.IRemotePropertyListener;
import shared.Message;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAdministration extends Remote
{
    long login(String username, String password, IRemotePropertyListener listener) throws RemoteException;
    long register(String username, String password, IRemotePropertyListener listener) throws RemoteException;
    void logout(long sessionId) throws RemoteException, InvalidArgumentException;
    boolean addContact(long sessionId, String contactName) throws RemoteException, InvalidArgumentException;
    void removeContact(long sessionId, String contactName) throws RemoteException, InvalidArgumentException;
    void newChat(long sessionId, String contact) throws RemoteException, InvalidArgumentException;
    void sendMessage(long sessionId, long chatId, Message message) throws RemoteException, InvalidArgumentException;
    List<Message> getChatMessages(long sessionId, long chatId) throws RemoteException, InvalidArgumentException;
    List<Long> getParticipatingChats(long sessionId) throws RemoteException, InvalidArgumentException;
    List<String> getContacts(long sessionId) throws RemoteException, InvalidArgumentException;
    byte[] getFile(long sessionId, long chatId, String filename) throws RemoteException, InvalidArgumentException, FileNotFoundException;
}
