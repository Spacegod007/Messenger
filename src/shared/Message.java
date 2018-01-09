package shared;

import server.logic.User;

import java.time.OffsetDateTime;

public abstract class Message<V>
{
    private final User author;
    private final V contents;
    private OffsetDateTime timestamp;

    public Message(V contents, User author)
    {
        this.author = author;
        this.contents = contents;
        this.timestamp = OffsetDateTime.now();
    }

    public Message(OffsetDateTime timestamp, V contents, User author)
    {
        this(contents, author);
        this.timestamp = timestamp;
    }

    public User getAuthor()
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
        return author.getUsername() + " " + timestamp.toLocalDateTime().toString() + String.format("%n") +
                contents.toString();
    }
}
