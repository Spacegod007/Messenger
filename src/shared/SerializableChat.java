package shared;

import server.logic.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableChat implements Serializable
{
    private final List<String> participants;
    private final List<Message> messages;
    private final long chatId;

    private final String chatSubscriptionName;

    public SerializableChat(long chatId, List<User> participants, List<Message> messages, String chatSubscriptionName)
    {
        this.messages = messages;
        this.chatId = chatId;
        this.chatSubscriptionName = chatSubscriptionName;

        this.participants = new ArrayList<>();
        participants.forEach(user -> this.participants.add(user.getUsername()));
    }

    public List<String> getParticipants()
    {
        return participants;
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    public long getChatId()
    {
        return chatId;
    }

    public String getChatSubscriptionName()
    {
        return chatSubscriptionName;
    }

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
