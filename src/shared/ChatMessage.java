package shared;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * A message with uses the String type as contents
 */
public class ChatMessage extends Message<String> implements Serializable
{
    /**
     * Constructor of the chat message
     * @param contents of the message
     * @param author of the message
     */
    public ChatMessage(String contents, String author)
    {
        super(contents, author);

        checkContentsAsString(contents);
    }

    /**
     * Constructor of the chat message which manually selects the date of creation
     * @param timestamp when the message was created
     * @param contents of the message
     * @param author of the message
     */
    public ChatMessage(OffsetDateTime timestamp, String contents, String author)
    {
        super(timestamp, contents, author);

        checkContentsAsString(contents);
    }

    private void checkContentsAsString(String contents)
    {
        if (contents == null || contents.isEmpty())
        {
            throw new IllegalArgumentException("contents cannot be null or empty");
        }
    }
}
