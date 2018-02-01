package co.matisses.webintegrator.ejb;

/**
 *
 * @author dbotero
 */
class ItemOutOfStockException extends Exception {

    public ItemOutOfStockException() {
    }

    public ItemOutOfStockException(String message) {
        super(message);
    }

}
