package domain.validators;

import domain.Friendship;

/**
 * Validator for Friendship entities
 */
public class FriendshipValidator implements Validator<Friendship> {
    /**
     * This method validates the friendship given as a parameter
     * @param entity representing the entity that is going to get validated
     * @throws ValidationException if the friendship is not valid
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getBuddy1() <= 0L){
            throw new ValidationException("Id for friend 1 can't be <=0");
        }
        if(entity.getBuddy2() <= 0L){
            throw new ValidationException("Id for friend 2 can't be <=0");
        }
    }
}
