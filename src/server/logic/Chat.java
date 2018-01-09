package server.logic;

import fileserver.logic.IFileStorage;
import shared.fontyspublisher.IRemotePropertyListener;
import shared.fontyspublisher.IRemotePublisherForListener;
import shared.fontyspublisher.RemotePublisher;
import shared.FileMessage;
import shared.Message;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Chat implements Serializable, IRemotePublisherForListener
{
    private final RemotePublisher publisher;
    public static final String MESSAGES_PROPERTY_NAME = "MESSAGES";

    private static final AtomicLong nextChatId = new AtomicLong(0);

    private final long chatId;

    private final List<Message> messages;
    private final List<User> participants;

    private final IFileStorage fileStorage;

    Chat(User self, User other) throws RemoteException
    {
        long tempId = nextChatId.incrementAndGet();
        while (tempId > (Long.MAX_VALUE - 2))
        {
            nextChatId.set(0);
        }
        chatId = nextChatId.incrementAndGet();

        this.messages = new ArrayList<>();
        this.participants = new ArrayList<>();

        participants.addAll(Arrays.asList(self, other));

        other.addToChat(this);

        publisher = new RemotePublisher();
        publisher.registerProperty(MESSAGES_PROPERTY_NAME);

        subscribeRemoteListener(self.getListener(), MESSAGES_PROPERTY_NAME);
        subscribeRemoteListener(other.getListener(), MESSAGES_PROPERTY_NAME);

        fileStorage = new FileServerClient().getFileStorage();
    }

    public void sendMessage(Message message) throws RemoteException
    {
        if (message instanceof FileMessage)
        {
            storeFile((FileMessage) message);
        }

        messages.add(message);

        publisher.inform(MESSAGES_PROPERTY_NAME, null, messages);
    }

    public byte[] getFile(String filename) throws FileNotFoundException, RemoteException
    {
        return fileStorage.getFile(filename);
    }

    private void storeFile(FileMessage fileMessage) throws RemoteException
    {
        fileStorage.storeData(fileMessage.getFilename(), fileMessage.getContents());
    }

    public long getChatId()
    {
        return chatId;
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    @Override
    public void subscribeRemoteListener(IRemotePropertyListener listener, String property) throws RemoteException
    {
        publisher.subscribeRemoteListener(listener, property);
    }

    @Override
    public void unsubscribeRemoteListener(IRemotePropertyListener listener, String property) throws RemoteException
    {
        publisher.unsubscribeRemoteListener(listener, property);
    }

    @Override
    public String toString()
    {
        StringBuilder returnable = new StringBuilder(String.valueOf(chatId));

        for (User user : participants)
        {
            returnable.append(user.toString());
        }

        return returnable.toString();
    }
}
