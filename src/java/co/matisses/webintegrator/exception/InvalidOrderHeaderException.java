package co.matisses.webintegrator.exception;

/**
 *
 * @author dbotero
 */
public class InvalidOrderHeaderException extends InvalidOrderException{

    public InvalidOrderHeaderException() {
        super();
    }

    public InvalidOrderHeaderException(String message) {
        super(message);
    }

    public InvalidOrderHeaderException(Throwable cause) {
        super(cause);
    }

    public InvalidOrderHeaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
