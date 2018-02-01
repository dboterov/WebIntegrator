package co.matisses.webintegrator.exception;

/**
 *
 * @author dbotero
 */
public class OutOfStockException extends Exception{

    public OutOfStockException() {
        super();
    }

    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(Throwable cause) {
        super(cause);
    }

    public OutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
