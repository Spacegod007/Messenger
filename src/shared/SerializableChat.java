package shared;

import server.logic.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A serializable (send able) version of a chat
 */
public class SerializableChat implements Serializable
{
    /**
     * a list of the participants of the chat
     */
    private final List<String> participants;

    /**
     * a list of all messages send
     */
    private final List<Message> messages;

    /**
     * the chat id of the chat
     */
    private final long chatId;

    /**
     * the name used to subscribe to the chat
     */
    private final String chatSubscriptionName;

    public SerializableChat(long chatId, List<User> participants, List<Message> messages, String chatSubscriptionName)
    {
        checkValues(chatId, participants, messages, chatSubscriptionName);

        this.messages = messages;
        this.chatId = chatId;
        this.chatSubscriptionName = chatSubscriptionName;

        this.participants = new ArrayList<>();
        participants.forEach(user -> this.participants.add(user.getUsername()));
    }

    private void checkValues(long chatId, List<User> participants, List<Message> messages, String chatSubscriptionName)
    {
        if (chatId < 0)
        {
            throw new IndexOutOfBoundsException("ChatId must be a positive number");
        }

        if (participants == null || participants.isEmpty() || participants.size() < 2)
        {
            throw new IllegalArgumentException("Participants must contain at least 2 users");
        }

        if (messages == null)
        {
            throw new IllegalArgumentException("Messages cannot be null");
        }

        if (chatSubscriptionName == null || chatSubscriptionName.isEmpty())
        {
            throw new IllegalArgumentException("chatSubscriptionName cannot be null or empty");
        }
    }

    /**
     * gets the participants of the chat
     * @return list of strings containing the names of participants
     */
    public List<String> getParticipants()
    {
        return participants;
    }

    /**
     * gets the messages send in the chat
     * @return list of message objects
     */
    public List<Message> getMessages()
    {
        return messages;
    }

    /**
     * gets the chat id
     * @return a long value containing the chat id
     */
    public long getChatId()
    {
        return chatId;
    }

    /**
     * gets the subscription name of the chat
     * @return a string containing the subscription name of the chat
     */
    public String getChatSubscriptionName()
    {
        return chatSubscriptionName;
    }

    /**
     * gets the name of the chat
     * @param username of the logged in user who is viewing this chat
     * @return a string containing the name of this chat
     */
    public String getName(String username)
    {
        StringBuilder returnable = new StringBuilder();

        for (String selectedUsername : participants)
        {
            if (!username.equals(selectedUsername))
            {
                if (returnable.length() == 0)
                {
                    returnable = new StringBuilder(selectedUsername);
                }
                else
                {
                    returnable.append(", ").append(selectedUsername);
                }
            }
        }

        return returnable.toString();
    }
}
