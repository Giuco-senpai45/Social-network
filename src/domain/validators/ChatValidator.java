package domain.validators;

import domain.Chat;

public class ChatValidator implements Validator<Chat> {
    @Override
    public void validate(Chat chat) throws ValidationException {
        if(chat.getId() == null){
            throw new ValidationException("Chat id can't be null");
        }
    }
}
