package service.serviceExceptions;

/**
 * This custom exception extends the RuntimeException class
 */
public class FindException extends RuntimeException{
    /**
     * default constructor
     */
    public FindException() {
    }

    /**
     * Overloaded constructor
     * @param message String representing the message
     */
    public FindException(String message) {
        super(message);
    }

    /**
     * Overloaded constructor
     * @param message String
     * @param cause Throwable
     */
    public FindException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Overloaded constructor
     * @param cause throwable
     */
    public FindException(Throwable cause) {
        super(cause);
    }

    /**
     * Overloaded constructor
     * @param message the message of the error
     * @param cause throwable cause
     * @param enableSuppression boolean
     * @param writableStackTrace boolean
     */
    public FindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

