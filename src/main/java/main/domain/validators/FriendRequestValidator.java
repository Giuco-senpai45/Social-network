package main.domain.validators;

import main.domain.FriendRequest;

import java.util.Objects;

/**
 * Validator for FriendRequest entities
 */
public class FriendRequestValidator implements Validator<FriendRequest> {
    /**
     * This function checks if the FriendRequest entity given as a parameter is valid or not
     * @param entity representing the entity that is going to get validated
     * @throws ValidationException if the entity is not valid
     */
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if(entity.getId() < 0 || entity.getId() == null){
            throw new ValidationException("Request id must be positive and not null");
        }
        if(entity.getFrom() == null){
            throw new ValidationException("Sender id must not be null");
        }
        if(entity.getTo() == null){
            throw new ValidationException("Receiver id must not be null");
        }
        if(!Objects.equals(entity.getStatus(), "pending") &&
            !Objects.equals(entity.getStatus(), "approved") &&
            !Objects.equals(entity.getStatus(), "rejected")){
            throw new ValidationException("Status must be: pending, approved or rejected");
        }
    }
}
