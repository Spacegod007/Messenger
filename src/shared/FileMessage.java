package shared;

import server.logic.User;

import java.time.OffsetDateTime;

public class FileMessage extends Message<byte[]>
{
    private final String filename;

    public FileMessage(byte[] contents, String filename, User author)
    {
        super(contents, author);
        this.filename = filename;
    }

    public FileMessage(OffsetDateTime timestamp, byte[] contents, String filename, User author)
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
        return getAuthor().getUsername() + " " + getTimestamp().toLocalDateTime().toString() + String.format("%n") +
                "Send: " + filename;
    }
}
