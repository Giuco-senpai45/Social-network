package domain.validators;

import domain.User;

/**
 * Validator for User entities
 */
public class UserValidator implements Validator<User> {
    /**
     * The validate method for validating the User
     * @param entity representing the entity that is going to get validated
     * @throws ValidationException if the user is not valid
     */
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getFirstName() == ""){
            throw new ValidationException("FirstName must not be null!");
        }
        if(entity.getLastName() == ""){
            throw new ValidationException("LastName must not be null!");
        }
    }
}