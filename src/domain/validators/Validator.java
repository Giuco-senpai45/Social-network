package domain.validators;

/**
 * @param <T> Representing a type of object that is going to get validated
 *           This class uses the Strategy pattern
 */
public interface Validator<T> {
    /**
     * @param entity  representing the entity that is going to get validated
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}