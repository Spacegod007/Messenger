package shared;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class FileMessage extends Message<byte[]> implements Serializable
{
    private final String filename;

    public FileMessage(byte[] contents, String filename, String author)
    {
        super(contents, author);
        this.filename = filename;
    }

    public FileMessage(OffsetDateTime timestamp, byte[] contents, String filename, String author)
    {
        super(timestamp, contents, author);
        this.filename = filename;
    }

    public String getFilename()
    {
        return filename;
    }

    @Override
    public String toString()
    {
        return getAuthor() + " " + getTimestamp().toLocalDateTime().toString() + String.format("%n") +
                "Send: " + filename;
    }
}
