package co.matisses.webintegrator.exception;

/**
 *
 * @author dbotero
 */
public class CustomerAddressMissingException extends InvalidOrderException{

    public CustomerAddressMissingException() {
    }

    public CustomerAddressMissingException(String message) {
        super(message);
    }

    public CustomerAddressMissingException(Throwable cause) {
        super(cause);
    }

    public CustomerAddressMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
