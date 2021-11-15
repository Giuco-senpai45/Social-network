package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;
import service.serviceExceptions.AddException;
import service.serviceExceptions.FindException;
import service.serviceExceptions.RemoveException;
import service.serviceExceptions.UpdateException;

import java.util.ArrayList;

/**
 * The service for the User entities
 */
public class UserService {
    /**
     * Repository for user entities
     */
    private Repository<Long, User> repoUsers;
    /**
     * Repository for friendship entities
     */
    private Repository<Tuple<Long,Long>, Friendship> repoFriends;

    /**
     * Represents an automated id.
     * This id will be equal to the maximum id present in the repo
     */
    private Long currentUserID = 0L;

    /**
     * Overloaded constructor
     * @param repoUsers repository for user entities
     * @param repoFriends repository for friendship entities
     */
    public UserService(Repository<Long, User> repoUsers, Repository<Tuple<Long,Long>, Friendship> repoFriends) {
        this.repoUsers = repoUsers;
        this.repoFriends = repoFriends;

        for(User user : repoUsers.findAll())
        {
            if(currentUserID < user.getId())
                currentUserID = user.getId();
        }
        currentUserID ++;

    }

    private void findMaximumId(){
        currentUserID = 0L;
        for(User user : repoUsers.findAll())
        {
            if(currentUserID < user.getId())
                currentUserID = user.getId();
        }
        currentUserID ++;
    }

    /**
     * This function creates a User entity and adds it to the repo
     * @param firstName String representing the first name of the user
     * @param lastName String representing the last name of the user
     * @throws AddException that user already exists
     */
    public void addUser(String firstName, String lastName){
        User user = new User(firstName, lastName);
        findMaximumId();
        user.setId(currentUserID);
        User addedUser = repoUsers.save(user);
        if(addedUser != null){
            throw new AddException("User already exists!");
        }
        else{
            System.out.println("User added cu with success");
//            currentUserID++;
        }
    }

    /**
     * This function removes the user with the given id
     * @param id representing the id of a user
     * @throws RemoveException if there doesn't exist an entity with the given id
     */
    public void removeUser(Long id){
        User user = repoUsers.delete(id);
        if(user == null){
            throw new RemoveException("User does not exist!");
        }
        System.out.println(user.toString() + " deleted");
    }

    /**
     * This function updates an existing user
     * @param firstName String representing the new firstName
     * @param lastName String representing the new lastName
     */
    public void updateUser(Long id,String firstName,String lastName){
        User updatedUser = new User(firstName,lastName);
        updatedUser.setId(id);
        User user = repoUsers.update(updatedUser);
        System.out.println(user);
        if(user != null){
            throw new UpdateException("This user doesn't exist");
        }
        else{
            System.out.println("User updated with success");
        }
    }

    public User findUserById(Long id){
        User user = repoUsers.findOne(id);
        if(user == null){
            throw new FindException("No user with the specified id exists");
        }
        System.out.println("User found");
        return user;
    }

    /**
     * This function returns all the users present in the repo
     * @return iterable representing all users
     */
    public Iterable<User> getUsers(){
        return repoUsers.findAll();
    }
}
