package repository.file;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;

import java.util.List;

/**
 * This class extends the AbstractFileRepository class and implements the
 *  -extractEntity
 *  -createEntityAsString
 *  methods
 *  This class creates FriendShip entities
 */
public class FriendshipFile extends AbstractFileRepository<Tuple<Long,Long>, Friendship>{

    /**
     * @param fileName String representing the file name
     * @param validator Validator representing the validator for FriendShip entities
     */
    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    /**
     * This function creates a Friendship entity with the List of attributes given as a parameter
     * @param attributes a list of strings representing the fields of the entity
     * @return Friendship entity
     */
    @Override
    protected Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship(Long.parseLong(attributes.get(0)),Long.parseLong(attributes.get(1)));
//        friendship.setId(Long.parseLong(attributes.get(0)));
        friendship.setId(new Tuple(Long.parseLong(attributes.get(0)),Long.parseLong(attributes.get(1))));
        return friendship;
    }

    /**
     * This function transforms a FriendShip entity into a String to be saved to the file
     * @param entity representing the entity that is going to get transformed into a String
     * @return String representing the entity as a string
     */
    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getBuddy1() + ";" + entity.getBuddy2();
    }

}
