package main.repository.file;

import java.util.List;
import main.domain.User;
import main.domain.validators.Validator;

/**
 * This class extends the AbstractFileRepository class and implements the
 *  -extractEntity
 *  -createEntityAsString
 *  methods
 *  This class creates FriendShip entities
 */
public class UserFile extends AbstractFileRepository<Long, User> {

    /**
     * @param fileName String representing the file name
     * @param validator Validator representing the validator for FriendShip entities
     */
    public UserFile(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    /**
     * This function creates a User entity with the List of attributes given as a parameter
     * @param attributes a list of strings representing the fields of the entity
     * @return User entity
     */
    @Override
    protected  User extractEntity(List<String> attributes) {
//        User user = new User(attributes.get(1),attributes.get(2));
//        user.setId(Long.parseLong(attributes.get(0)));
//        return user;
        return null;
    }

    /**
     * This function transforms a User entity into a String to be saved to the file
     * @param entity representing the entity that is going to get transformed into a String
     * @return String representing the entity as a string
     */
    @Override
    protected String createEntityAsString(User entity) {
        String line = entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
        return line;
    }
}
