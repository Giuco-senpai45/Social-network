package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import graph.Graph;
import repository.Repository;
import service.serviceExceptions.AddException;
import service.serviceExceptions.FindException;
import service.serviceExceptions.RemoveException;
import java.util.ArrayList;


/**
 *  Service class for the Friendship entities.
 */
public class FriendshipService {
    /**
     * Repository for friendship entities
     */
    private Repository<Tuple<Long,Long> , Friendship> repoFriends;

    /**
     * Repository for user entities
     */
    private Repository<Long, User> repoUsers;

    /**
     * Instance of the Graph class
     */
    private Graph graph;

    /**
     * Overloaded constructor
     * @param repoFriends repository for friendship entities
     * @param repoUsers repository for user entities
     */
    public FriendshipService(Repository<Tuple<Long,Long>, Friendship> repoFriends, Repository<Long, User> repoUsers) {
        this.repoFriends = repoFriends;
        this.repoUsers = repoUsers;
        updateFriendList();
    }

    /**
     * This function creates a valid friendship between 2 existing
     * @param buddy1 long representing the first friend in the friendship
     * @param buddy2 long representing the second friend in the friendship
     * @throws AddException if the users with the specified ids don't exist , if the
     */
    public void addFriendship(Long buddy1, Long buddy2){

        //Check if the reverse friendship exists
        Tuple<Long,Long> tuple = new Tuple<>(buddy2,buddy1);
        if(repoFriends.findOne(tuple) != null){
            throw new AddException("This friendship already exists");
        }

        Iterable<User> iterator = getUsers();
        boolean foundFriend1 = false;
        boolean foundFriend2 = false;

        for(User user : iterator){
            if(user.getId() == buddy1)
                foundFriend1 = true;
            else if(user.getId() == buddy2)
                foundFriend2 = true;
        }

        if(foundFriend1 && foundFriend2){
            Friendship friendship = new Friendship(buddy1,buddy2);
            Friendship addedFriendship = repoFriends.save(friendship);
            if (addedFriendship != null) {
                throw new AddException("This friendship already exists");
            }
            updateFriendList();
        }
        else {
            throw new AddException("The specified users don't exist");
        }
    }

    /**
     * This function removes a friendship from the repo
     * @param id Tuple of 2 Longs representing the friendship that is going to get removed
     * @throws RemoveException if there doesn't exist any friendship with the given Tuple
     */
    public void removeFriendship(Tuple<Long,Long> id){
        Friendship friendship = repoFriends.delete(id);
        if(friendship == null){
            throw new RemoveException("This friendship doesn't exist");
        }
        System.out.println(friendship.toString() + " removed");
        updateFriendList();
    }

    /**
     * This function returns the friendships present in the repo
     * @return Iterable representing the friendships currently stored in the repo
     */
    public Iterable<Friendship> getFriendships(){
        return repoFriends.findAll();
    }

    /**
     * This function initializes the Graph with the list of current friendships and users
     */
    private void initGraph(){
//        int size = (int) StreamSupport.stream(getUsers().spliterator(), false).count();
        updateFriendList();
        this.graph = new Graph(repoFriends.findAll(),repoUsers.findAll());
    }

    /**
     * This function finds the number of connected components present in the Graph constructed with the current friendships
     * @return integer representing the number of connected components
     */
    public int findConnectedComponents(){
        initGraph();
        return graph.noOfComponents();
    }

    /**
     * This function returns the most sociable community
     * @return an ArrayList representing the most sociable community
     */
    public ArrayList<Integer> mostSociableCommunity(){
        initGraph();
        ArrayList<Integer> components = graph.getLongestPathComponents();
        return  components;
    }

    /**
     * This function updates for each User in the user repository its friend list
     */
    private void updateFriendList(){
        for(User user : getUsers()){
            ArrayList<User> friends = new ArrayList();
            for(Friendship friendship : repoFriends.findAll()){
                if(user.getId() == friendship.getBuddy1()){
                    friends.add(repoUsers.findOne(friendship.getBuddy2()));
                }
                else if(user.getId() == friendship.getBuddy2()){
                    friends.add(repoUsers.findOne(friendship.getBuddy1()));
                }
            }
            if(friends != null){
                user.setFriends(friends);
            }
        }
    }

    public Friendship findFriendshipById(Tuple<Long,Long> tuple){
        Friendship friendship = repoFriends.findOne(tuple);
        if(friendship == null){
            Tuple<Long,Long> tupleRev = new Tuple<>(tuple.getE2(),tuple.getE1());
            Friendship friendshipReverse = repoFriends.findOne(tupleRev);
            if(friendshipReverse == null){
                throw new FindException("Friendship doesn't exist");
            }
            System.out.println("Friendship found");
            return friendshipReverse;
        }
        System.out.println("Friendship found");
        return friendship;
    }

    /**
     * This function returns all the users present in the repo
     * @return iterable representing all users
     */
    public Iterable<User> getUsers(){
        return repoUsers.findAll();
    }
}
