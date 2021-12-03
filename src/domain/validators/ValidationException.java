package domain.validators;

/**
 * This custom exception extends the RuntimeException class
 */
public class ValidationException extends RuntimeException {
    /**
     * default constructor
     */
    public ValidationException() {
    }

    /**
     * Overloaded constructor
     * @param message String representing the message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Overloaded constructor
     * @param message String
     * @param cause Throwable
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Overloaded constructor
     * @param cause throwable
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * Overloaded constructor
     * @param message the message of the error
     * @param cause throwable cause
     * @param enableSuppression boolean
     * @param writableStackTrace boolean
     */
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
