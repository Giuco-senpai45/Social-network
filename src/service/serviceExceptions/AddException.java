package service.serviceExceptions;

/**
 * This custom exception extends the RuntimeException class
 */
public class AddException extends RuntimeException{
    /**
     * default constructor
     */
    public AddException() {
    }

    /**
     * Overloaded constructor
     * @param message String representing the message
     */
    public AddException(String message) {
        super(message);
    }

    /**
     * Overloaded constructor
     * @param message String
     * @param cause Throwable
     */
    public AddException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Overloaded constructor
     * @param cause throwable
     */
    public AddException(Throwable cause) {
        super(cause);
    }

    /**
     * Overloaded constructor
     * @param message the message of the error
     * @param cause throwable cause
     * @param enableSuppression boolean
     * @param writableStackTrace boolean
     */
    public AddException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
