package main.service;

import main.domain.*;
import main.repository.Repository;
import main.service.serviceExceptions.AddException;
import main.service.serviceExceptions.FindException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for handling friend requests
 */
public class FriendRequestService {
    /**
     * Repository for Friendship entities
     */
    private Repository<Tuple<Long,Long>, Friendship> repoFriends;
    /**
     * Repository for User entities
     */
    private Repository<Long, User> repoUsers;
    /**
     * Repository for FriendRequest entities
     */
    private Repository<Long, FriendRequest> repoRequests;

    /**
     * Overloaded constructor that takes arguments 3 repositories
     * @param repoFriends Repo for Friendships
     * @param repoUsers Repo for Users
     * @param repoRequests Repo for Requests
     */
    public FriendRequestService(Repository<Tuple<Long, Long>, Friendship> repoFriends,
                                Repository<Long, User> repoUsers,
                                Repository<Long, FriendRequest> repoRequests) {
        this.repoFriends = repoFriends;
        this.repoUsers = repoUsers;
        this.repoRequests = repoRequests;
    }

    /**
     * This function sends a friend request from the currently logged user to another user present in the
     * system.
     * @param from Long representing the id of User who sent the friend request
     * @param to Long representing the id of User who receives the friend request
     * @return boolean  (true if the request was sent successfully / false if the request couldn't be sent)
     */
    public boolean sendRequest(Long from,Long to) {
        if(Objects.equals(from, to)){
            throw new AddException("You can't befriend yourself, you really should talk to more people :(");
        }
        Long nextID = maximReqId() + 1;
        if(repoUsers.findOne(from) != null && repoUsers.findOne(to) !=null){
            FriendRequest request = new FriendRequest(from,to,"pending");
            request.setId(nextID);
            System.out.println(request.getId());
            Iterable<FriendRequest> requests = repoRequests.findAll();
            FriendRequest foundRequest = null;
            for(FriendRequest friendRequest : requests){
                if(friendRequest.getFrom() == from && friendRequest.getTo() == to){
                    foundRequest = friendRequest;
                }
                else if(friendRequest.getFrom() == to && friendRequest.getTo() == from){
                    foundRequest = friendRequest;
                }
            }
            if(foundRequest == null){
                repoRequests.save(request);
                return true;
            }
            else if (foundRequest.getStatus().equals("rejected")){
                processRequest(request.getId(),"pending");
                return true;
            }
            else {
                throw new AddException("Request already exists");
            }
        }
        else {
            throw new FindException("Couldn't find the user with the specified id");
        }
    }

    /**
     * This function updates a pending FriendRequest with one of the 2 statuses:
     *  -approved if the logged User wants to accept the incoming friend request
     *  -rejected if the logged User wants to reject the request
     * @param id Long representing the FriendRequests ID
     * @param newStatus String representing the status of the request (approved / rejected)
     * @return boolean (true if the request was accepted / false if the request was rejected)
     * @throws FindException if there exists no request with the specified iD
     */
    public boolean processRequest(Long id,String newStatus){
        FriendRequest request = repoRequests.findOne(id);
        if(request != null){
            request.setStatus(newStatus);
            repoRequests.update(request);
            Friendship friendship = new Friendship(request.getFrom(), request.getTo());
            if(newStatus.equals("approved")) {
                repoFriends.save(friendship);
                return true;
            }
            else if(newStatus.equals("rejected")){
                return false;
            }
        }
        else{
            throw new FindException("Couldn't find the specified friend request");
        }
        return false;
    }

    /**
     * This function returns a list of FriendRequests sent to the currently logged USer
     * @param id Long representing the logged Users id
     * @return List of FriendRequests representing pending requests for the current User
     */
    public List<FriendRequest> findPendingRequestsForUser(Long id){
        Iterable<FriendRequest> requests = repoRequests.findAll();
        List<FriendRequest> requestsList = new ArrayList<>();
        requests.forEach(requestsList::add);

        Predicate<FriendRequest> testIsPending = fr -> fr.getStatus().equals("pending");
        Predicate<FriendRequest> testIsForUser = fr -> fr.getTo().equals(id);
        Predicate<FriendRequest> testValid = testIsForUser.and(testIsPending);

        return requestsList.stream()
                .filter(testValid)
                .collect(Collectors.toList());
    }

    public List<FriendRequest> getHistoryRequests(Long id){
        Iterable<FriendRequest> requests = repoRequests.findAll();
        List<FriendRequest> requestsList = new ArrayList<>();
        requests.forEach(requestsList::add);

        Predicate<FriendRequest> testIsValid = fr -> fr.getStatus().equals("approved") || fr.getStatus().equals("rejected");

        return requestsList.stream()
                .filter(testIsValid)
                .collect(Collectors.toList());
    }

    /**
     * This function returns the maximum ID found in the database
     * @return Long representing the biggest id found in the database
     */
    private Long maximReqId() {
        Long maxID = 0L;
        for(FriendRequest friendRequest: repoRequests.findAll()){
            if(friendRequest.getId() > maxID)
                maxID = friendRequest.getId();
        }
        return maxID;
    }
}
