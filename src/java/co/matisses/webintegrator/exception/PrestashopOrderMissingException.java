package co.matisses.webintegrator.exception;

/**
 *
 * @author dbotero
 */
public class PrestashopOrderMissingException extends InvalidOrderException{

    public PrestashopOrderMissingException() {
    }

    public PrestashopOrderMissingException(String message) {
        super(message);
    }

    public PrestashopOrderMissingException(Throwable cause) {
        super(cause);
    }

    public PrestashopOrderMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
