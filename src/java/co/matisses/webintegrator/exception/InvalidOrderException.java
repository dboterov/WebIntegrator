package co.matisses.webintegrator.exception;

/**
 *
 * @author dbotero
 */
public class InvalidOrderException extends Exception{

    public InvalidOrderException() {
        super();
    }

    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(Throwable cause) {
        super(cause);
    }

    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
