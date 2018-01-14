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

/**
 * A chat where users can talk to each other
 */
public class Chat
{
    /**
     * The subscription name of the object clients need to have to get updates
     */
    private final String chatSubscriptionName;

    /**
     * the next chat id
     */
    private static final AtomicLong nextChatId = new AtomicLong(0);

    /**
     * the identifier of the chat
     */
    private final long chatId;

    /**
     * A list of messages in the chat
     */
    private final List<Message> messages;

    /**
     * A list of participants in the chat
     */
    private final List<User> participants;

    /**
     * The filestorage where files are stored
     */
    private final IFileStorage fileStorage;

    /**
     * The contructor of the chat
     * @param self user who created the chat
     * @param other user who is invited to the chat
     * @throws RemoteException if something goes wrong in the connection to the filestorage
     */
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

    /**
     * Gets the subscription name of the chat
     * @return
     */
    public String getChatSubscriptionName()
    {
        return chatSubscriptionName;
    }

    /**
     * Gets a serializable (sendable) version of the chat
     * @return a SerializableChat object
     */
    public SerializableChat getAsSerializable()
    {
        return new SerializableChat(chatId, participants, messages, chatSubscriptionName);
    }

    /**
     * Sends a message in this chat
     * @param message to be send
     * @throws RemoteException if something goes wrong in the connection to the file storage (should the message contain a file)
     */
    public void sendMessage(Message message) throws RemoteException
    {
        if (message instanceof FileMessage)
        {
            storeFile((FileMessage) message);
        }

        messages.add(message);

        informParticipants();
    }

    /**
     * Gets the specified file of of the filestorage
     * @param filename to be retrieved
     * @return a byte array containing the file data
     * @throws FileNotFoundException if the file cannot be found
     * @throws RemoteException if something goes wrong in the connection to the file storage
     */
    private byte[] getFile(String filename) throws FileNotFoundException, RemoteException
    {
        return fileStorage.getFile(filename);
    }

    /**
     * Stores a file on the filestorage
     * @param fileMessage to be stored
     */
    private void storeFile(FileMessage fileMessage)
    {
        try
        {
            fileStorage.storeData(fileMessage.getFilename(), fileMessage.getContents());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the chat id
     * @return a long variable containing the chat id
     */
    public long getChatId()
    {
        return chatId;
    }

    /**
     * Informs all participants of a change in the chat
     */
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
