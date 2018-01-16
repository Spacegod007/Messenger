package shared;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;

public class FileMessageTest
{
    private byte[] data;
    private String fileName;
    private String author;

    private OffsetDateTime offsetDateTime;

    private FileMessage fileMessage;
    private FileMessage fileMessage2;

    @Before
    public void setUp() throws Exception
    {
        data = new byte[] { 1, 2, 3, 4 };
        fileName = "name";
        author = "author";
        offsetDateTime = OffsetDateTime.now();

        fileMessage = new FileMessage(data, fileName, author);
        fileMessage2 = new FileMessage(offsetDateTime, data, fileName, author);
    }

    @Test
    public void testConstruction() throws Exception
    {
        try
        {
            new FileMessage(null, fileName, author);
            Assert.fail("null should not be allowed for the contents value");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new FileMessage(data, null, author);
            Assert.fail("null should not be allowed for the filename value");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new FileMessage(data, fileName, null);
            Assert.fail("null should not be allowed for the author value");
        }
        catch (IllegalArgumentException ignored)
        { }

        try
        {
            new FileMessage(null, data, fileName, author);
            Assert.fail("null should not be allowed for the timestamp value");
        }
        catch (IllegalArgumentException ignored)
        { }
    }

    @Test
    public void getFilename() throws Exception
    {
        Assert.assertEquals("filename is not the assigned value from the constructor", fileName, fileMessage.getFilename());
        Assert.assertEquals("filename is not the assigned value from the constructor", fileName, fileMessage2.getFilename());
    }

    @Test
    public void getAuthor() throws Exception
    {
        Assert.assertEquals("author is not the assigned value from the constructor", author, fileMessage.getAuthor());
        Assert.assertEquals("author is not the assigned value from the constructor", author, fileMessage2.getAuthor());
    }

    @Test
    public void getContents() throws Exception
    {
        Assert.assertEquals("data is not equal to the contents from the constructor", data, fileMessage.getContents());
        Assert.assertEquals("data is not equal to the contents from the constructor", data, fileMessage2.getContents());
    }

    @Test
    public void getTimestamp() throws Exception
    {
        Assert.assertEquals("timestamp is not equal to the contents of the constructor", offsetDateTime, fileMessage2.getTimestamp());
    }

}