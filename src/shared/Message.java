package shared;

import java.io.Serializable;
import java.time.OffsetDateTime;

public abstract class Message<V> implements Serializable
{
    private final String author;
    private final V contents;
    private OffsetDateTime timestamp;

    public Message(V contents, String author)
    {
        this.author = author;
        this.contents = contents;
        this.timestamp = OffsetDateTime.now();
    }

    public Message(OffsetDateTime timestamp, V contents, String author)
    {
        this(contents, author);
        this.timestamp = timestamp;
    }

    public String getAuthor()
    {
        return author;
    }

    public V getContents()
    {
        return contents;
    }

    public OffsetDateTime getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String toString()
    {
        return "(" + timestamp.getHour() + ":" + timestamp.getMinute() + ") " + author + String.format(": %n") + contents.toString();
    }
}
