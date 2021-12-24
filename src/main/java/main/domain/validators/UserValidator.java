package main.domain.validators;

import main.domain.User;

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

    public boolean validateName(String name){
        String pattern = "^[A-Z]([a-z]+)$";
        return name.matches(pattern);
    }

    public boolean validateEmail(String name){
        String pattern = "^([a-z0-9]+)@([a-z]+).([a-z]+)$";
        return name.matches(pattern);
    }

    public boolean validatePassword(String text){
        String pattern = "^(.{8,})";
        return text.matches(pattern);
    }
}