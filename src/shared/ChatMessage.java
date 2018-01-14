package shared;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class ChatMessage extends Message<String> implements Serializable
{
    public ChatMessage(String contents, String author)
    {
        super(contents, author);
    }

    public ChatMessage(OffsetDateTime timestamp, String contents, String author)
    {
        super(timestamp, contents, author);
    }
}
