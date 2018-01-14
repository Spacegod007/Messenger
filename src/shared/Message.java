package shared;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * A message object
 * @param <V> used to determine the contents of the message
 */
public abstract class Message<V> implements Serializable
{
    /**
     * author of the message
     */
    private final String author;

    /**
     * the contents of the message
     */
    private final V contents;

    /**
     * the timestamp when the message was created
     */
    private OffsetDateTime timestamp;

    /**
     * the constructor of the message
     * @param contents of the message
     * @param author of the message
     */
    Message(V contents, String author)
    {
        this.author = author;
        this.contents = contents;
        this.timestamp = OffsetDateTime.now();
    }

    /**
     * the constructor of the message which manually selects the date of creation
     * @param timestamp when the message was created
     * @param contents of the message
     * @param author of the message
     */
    Message(OffsetDateTime timestamp, V contents, String author)
    {
        this(contents, author);
        this.timestamp = timestamp;
    }

    /**
     * gets the author of the message
     * @return a string containing the author of the message
     */
    String getAuthor()
    {
        return author;
    }

    /**
     * gets the contents of a message
     * @return an object containing the contents of the message
     */
    public V getContents()
    {
        return contents;
    }

    /**
     * gets the date and time the message was created
     * @return an OffsetDateTime object which is when the message was created
     */
    OffsetDateTime getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String toString()
    {
        return "(" + timestamp.getHour() + ":" + timestamp.getMinute() + ") " + author + String.format(": %n") + contents.toString();
    }
}
