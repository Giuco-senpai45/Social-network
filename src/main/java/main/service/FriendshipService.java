package main.service;

import main.domain.Friendship;
import main.domain.Tuple;
import main.domain.User;
import main.graph.Graph;
import main.repository.Repository;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.PageableImplementation;
import main.repository.paging.PagingRepository;
import main.service.serviceExceptions.AddException;
import main.service.serviceExceptions.FindException;
import main.service.serviceExceptions.RemoveException;
import main.utils.Observable;
import main.utils.Observer;
import main.utils.events.ChangeEventType;
import main.utils.events.FriendDeletionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 *  Service class for the Friendship entities.
 */
public class FriendshipService implements Observable<FriendDeletionEvent> {
    /**
     * Repository for friendship entities
     */
    private PagingRepository<Tuple<Long,Long> , Friendship> repoFriends;

    /**
     * Repository for user entities
     */
    private PagingRepository<Long, User> repoUsers;

    /**
     * Instance of the Graph class
     */
    private Graph graph;

    /**
     * Overloaded constructor
     * @param repoFriends repository for friendship entities
     * @param repoUsers repository for user entities
     */
    public FriendshipService(PagingRepository<Tuple<Long,Long>, Friendship> repoFriends, PagingRepository<Long, User> repoUsers) {
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
        Tuple<Long,Long> idreversed = new Tuple<>(id.getE2(),id.getE1());
        Friendship friendshipreversed = null;
        if(friendship == null){
            friendshipreversed = repoFriends.delete(idreversed);
            if(friendshipreversed == null) {
                throw new RemoveException("This friendship doesn't exist");
            }
        }
        if(friendship != null)
            notifyObservers(new FriendDeletionEvent(ChangeEventType.DELETE, friendship));
        else if (friendshipreversed != null){
            notifyObservers(new FriendDeletionEvent(ChangeEventType.DELETE, friendshipreversed));
        }
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


    private List<Observer<FriendDeletionEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendDeletionEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendDeletionEvent> e) {

    }

    @Override
    public void notifyObservers(FriendDeletionEvent t) {
        observers.stream().forEach(x->x.update(t));
    }


    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

//    public void setPageable(Pageable pageable) {
//        this.pageable = pageable;
//    }

    public Set<Friendship> getNextUsers() {
//        Pageable pageable = new PageableImplementation(this.page, this.size);
//        Page<MessageTask> studentPage = repo.findAll(pageable);
//        this.page++;
//        return studentPage.getContent().collect(Collectors.toSet());
        this.page++;
        return getFriendshipsOnPage(this.page);
    }

    public Set<Friendship> getFriendshipsOnPage(int page) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Friendship> friendsPage = repoFriends.findAll(pageable);
        return friendsPage.getContent().collect(Collectors.toSet());
    }

}
