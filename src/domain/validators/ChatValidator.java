package domain.validators;

import domain.Chat;

/**
 * Chat Validator
 */
public class ChatValidator implements Validator<Chat> {
    /**
     * This function checks if the Chat entity is valid
     * @param chat Chat entity that gets validated
     * @throws ValidationException if the entity is not valid
     */
    @Override
    public void validate(Chat chat) throws ValidationException {
        if(chat.getId() == null){
            throw new ValidationException("Chat id can't be null");
        }
    }
}
