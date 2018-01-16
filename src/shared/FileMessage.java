package shared;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Message with byte[] type implementation used to transfer files
 */
public class FileMessage extends Message<byte[]> implements Serializable
{
    /**
     * name of the file
     */
    private final String filename;

    /**
     * constructor of the file message object
     * @param contents of the file
     * @param filename of the file
     * @param author of the message
     */
    public FileMessage(byte[] contents, String filename, String author)
    {
        super(contents, author);

        checkFilename(filename);

        this.filename = filename;
    }

    private void checkFilename(String filename)
    {
        if (filename == null || filename.isEmpty())
        {
            throw new IllegalArgumentException("filename cannot be null or empty");
        }
    }

    /**
     * constructor of the file message object which manually selects the date of creation
     * @param timestamp when the message was created
     * @param contents of the file
     * @param filename of the file
     * @param author of the message
     */
    public FileMessage(OffsetDateTime timestamp, byte[] contents, String filename, String author)
    {
        super(timestamp, contents, author);

        checkFilename(filename);
        
        this.filename = filename;
    }

    /**
     * gets the name of the file
     * @return a string object containing the filename
     */
    public String getFilename()
    {
        return filename;
    }

    @Override
    public String toString()
    {
        return "(" + getTimestamp().getHour() + ":" + getTimestamp().getMinute() + ") " + getAuthor() + String.format(": %n") + filename;
    }
}
