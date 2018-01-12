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

public class Chat
{
    public static final String MESSAGES_PROPERTY_NAME = "Messages";
    public static final String PARTICIPANTS_CHECKER = "Participants";

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

        fileStorage = new FileServerClient().getFileStorage();
    }

    public void sendMessage(Message message) throws RemoteException
    {
        if (message instanceof FileMessage)
        {
            storeFile((FileMessage) message);
        }

        messages.add(message);
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
    public String toString()
    {
        StringBuilder returnable = new StringBuilder(String.valueOf(chatId));

        for (User user : participants)
        {
            returnable.append(user.toString());
        }

        return returnable.toString();
    }

    public String getChatName(String username)
    {
        StringBuilder returnable = new StringBuilder();

        for (User participant : participants)
        {
            if (!participant.getUsername().equals(username))
            {
                if (returnable.length() == 0)
                {
                    returnable = new StringBuilder(participant.getUsername());
                }
                else
                {
                    returnable.append(", ").append(participant.getUsername());
                }
            }
        }

        return returnable.toString();
    }
}
