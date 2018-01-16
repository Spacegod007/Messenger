package shared;

import bootstrapper.ServerProgram;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import server.logic.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class SerializableChatTest
{
    //due to it only being possible to create ServerProgram once, all methods below are linked from one onto another and testing all data

    private final ServerProgram serverProgram = new ServerProgram();

    private long chatId;
    private String subscriptionName;
    private User user1;
    private User user2;
    private List<User> participants;
    private List<Message> messages;

    private SerializableChat serializableChat;

    @Before
    public void setup() throws Exception
    {
        chatId = 200;
        subscriptionName = "a random subscription name that totally isn't accurate";

        user1 = new User("user1", "user1Password", serverProgram);
        user2 = new User("user2", "user2Password", serverProgram);

        participants = new ArrayList<>();
        participants.add(user1);
        participants.add(user2);

        messages = new ArrayList<>();

        try
        {
            serializableChat = new SerializableChat(chatId, participants, messages, subscriptionName);
        }
        catch (Exception e)
        {
            Assert.fail("a normally working serializable chat could not be created");
        }
    }

    @Test
    public void testConstruction() throws Exception
    {
        try
        {
            new SerializableChat(-1, participants, messages, subscriptionName);
            Assert.fail("chatId should not contain a value below 0");
        }
        catch (IndexOutOfBoundsException ignored)
        { }

        try
        {
            new SerializableChat(0, null, messages, subscriptionName);
            Assert.fail("participants should contain at least 2 people");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new SerializableChat(1, new ArrayList<>(), messages, subscriptionName);
            Assert.fail("participants should contain at least 2 people");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            List<User> tempList = new ArrayList<>();
            new SerializableChat(2, tempList, messages, subscriptionName);
            Assert.fail("participants should contain at least 2 people");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new SerializableChat(3, participants, null, subscriptionName);
            Assert.fail("messages should not be allowed to be null");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new SerializableChat(4, participants, messages, null);
            Assert.fail("chatSubscriptionName should not be allowed to be null");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new SerializableChat(5, participants, messages, "");
            Assert.fail("chatSubscriptionName, should contain at least 1 character");
        }
        catch (IllegalArgumentException ignored)
        { }

        getParticipants();
    }

    private void getParticipants() throws Exception
    {
        for (User participant : participants)
        {
            Assert.assertTrue("Participants does not contain the username of the given users", serializableChat.getParticipants().contains(participant.getUsername()));
        }

        if (serializableChat.getParticipants().size() > 2 || serializableChat.getParticipants().size() < 2)
        {
            Assert.fail("Only given users should be present in the list of participants");
        }

        getMessages();
    }

    private void getMessages() throws Exception
    {
        Assert.assertEquals("Messages does not contain the value given in the constructor", messages, serializableChat.getMessages());

        getChatId();
    }

    private void getChatId() throws Exception
    {
        Assert.assertEquals("ChatId does not contain the value given in the constructor", chatId, serializableChat.getChatId());

        getChatSubscriptionName();
    }

    private void getChatSubscriptionName() throws Exception
    {
        Assert.assertEquals("ChatSubscriptionName does not contain the value given in the constructor", subscriptionName, serializableChat.getChatSubscriptionName());

        getName();
    }

    private void getName() throws Exception
    {
        if (!serializableChat.getName(user1.getUsername()).contains(user2.getUsername()))
        {
            Assert.fail("The chat name should show for each user who they are talking to");
        }

        if (serializableChat.getName(user1.getUsername()).contains(user1.getUsername()))
        {
            Assert.fail("The username of the current user should not appear in the chat name");
        }

        User user3 = new User("user3", "user3Password", serverProgram);
        serializableChat.getParticipants().add(user3.getUsername());

        String user2ChatName = serializableChat.getName(user2.getUsername());

        if (!user2ChatName.contains(user1.getUsername()) && !user2ChatName.contains(user3.getUsername()))
        {
            Assert.fail("The chat name should show for each user who they are talking to");
        }

        if (user2ChatName.contains(user2.getUsername()))
        {
            Assert.fail("The username of the current user should not appear in the chat name");
        }
    }

}