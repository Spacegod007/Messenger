package shared;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;

public class ChatMessageTest
{
    private String contents;
    private String author;
    private OffsetDateTime timestamp;

    private ChatMessage message;
    private ChatMessage message2;

    @Before
    public void setUp() throws Exception
    {
        contents = "message";
        author = "author";
        timestamp = OffsetDateTime.now();

        message = new ChatMessage(contents, author);
        message2 = new ChatMessage(timestamp, contents, author);
    }

    @Test
    public void testConstruction() throws Exception
    {
        try
        {
            new ChatMessage(null, author);
            Assert.fail("null should not be allowed for the contents value");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new ChatMessage("", author);
            Assert.fail("an empty string should not be allowed for the contents value");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new ChatMessage(contents, null);
            Assert.fail("null should not be allowed for the author value");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new ChatMessage(null, contents, author);
            Assert.fail("null should not be allowed for the timestamp value");
        }
        catch (IllegalArgumentException ignored)
        { }
    }

    @Test
    public void getAuthor() throws Exception
    {
        Assert.assertEquals("Author is not the given value in the constructor", author, message.getAuthor());
        Assert.assertEquals("Author is not the given value in the constructor", author, message2.getAuthor());
    }

    @Test
    public void getContents() throws Exception
    {
        Assert.assertEquals("Contents is not the given value in the constructor", contents, message.getContents());
        Assert.assertEquals("Contents is not the given value in the constructor", contents, message.getContents());
    }

    @Test
    public void getTimestamp() throws Exception
    {
        Assert.assertEquals("Timestamp is not the given value in the constructor", timestamp, message.getTimestamp());
        Assert.assertEquals("Timestamp is not the given value in the constructor", timestamp, message.getTimestamp());
    }

}