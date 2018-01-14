package server.logic;

import fileserver.logic.IFileStorage;
import shared.SerializableChat;
import shared.FileMessage;
import shared.Message;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Chat
{
    private final String chatSubscriptionName;

    private static final AtomicLong nextChatId = new AtomicLong(0);

    private final long chatId;

    private final List<Message> messages;
    private final List<User> participants;

    private final IFileStorage fileStorage;

    Chat(User self, User other) throws RemoteException
    {
        long tempId = nextChatId.get();
        if (tempId > (Long.MAX_VALUE - 2))
        {
            nextChatId.set(0);
        }
        chatId = nextChatId.incrementAndGet();

        chatSubscriptionName = "chat_" + chatId;

        this.messages = new ArrayList<>();
        this.participants = new ArrayList<>();

        participants.addAll(Arrays.asList(self, other));

        for (User participant : participants)
        {
            participant.addToChat(this);
        }

        fileStorage = new FileServerClient().getFileStorage();
    }

    public String getChatSubscriptionName()
    {
        return chatSubscriptionName;
    }

    public SerializableChat getAsSerializable()
    {
        return new SerializableChat(chatId, participants, messages, chatSubscriptionName);
    }

    public void sendMessage(Message message) throws RemoteException
    {
        if (message instanceof FileMessage)
        {
            storeFile((FileMessage) message);
        }

        messages.add(message);

        informParticipants();
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

    private void informParticipants()
    {
        for (User participant : participants)
        {
            try
            {
                participant.inform(chatSubscriptionName, null, getAsSerializable());
            }
            catch (RemoteException ignored)
            { }
        }
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
