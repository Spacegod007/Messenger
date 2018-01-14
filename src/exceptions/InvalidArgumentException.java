package exceptions;

/**
 * Used when invalid arguments are presented
 */
public class InvalidArgumentException extends Exception
{
    public InvalidArgumentException(String message)
    {
        super(message);
    }
}
