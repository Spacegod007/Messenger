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
        authorAndDataCheck(contents, author);

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

        if (timestamp == null)
        {
            throw new IllegalArgumentException("a given timestamp should never be null");
        }

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

    /**
     * Checks if the author and contents contain a value
     * @param contents of the message
     * @param author of the message
     */
    private void authorAndDataCheck(V contents, String author)
    {
        if (contents == null)
        {
            throw new IllegalArgumentException("contents of a message cannot be null");
        }

        if (author == null || author.isEmpty())
        {
            throw new IllegalArgumentException("author of a message cannot be null");
        }
    }

    @Override
    public String toString()
    {
        return "(" + timestamp.getHour() + ":" + timestamp.getMinute() + ") " + author + String.format(": %n") + contents.toString();
    }
}
