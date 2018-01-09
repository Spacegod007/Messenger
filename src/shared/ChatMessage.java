package shared;

import server.logic.User;

import java.time.OffsetDateTime;

public class ChatMessage extends Message<String>
{
    public ChatMessage(String contents, User author)
    {
        super(contents, author);
    }

    public ChatMessage(OffsetDateTime timestamp, String contents, User author)
    {
        super(timestamp, contents, author);
    }
}
