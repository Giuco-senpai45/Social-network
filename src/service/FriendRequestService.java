package service;

import domain.*;
import repository.Repository;
import service.serviceExceptions.AddException;
import service.serviceExceptions.FindException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FriendRequestService {
    private Repository<Tuple<Long,Long>, Friendship> repoFriends;
    private Repository<Long, User> repoUsers;
    private Repository<Long, FriendRequest> repoRequests;

    public FriendRequestService(Repository<Tuple<Long, Long>, Friendship> repoFriends,
                                Repository<Long, User> repoUsers,
                                Repository<Long, FriendRequest> repoRequests) {
        this.repoFriends = repoFriends;
        this.repoUsers = repoUsers;
        this.repoRequests = repoRequests;
    }

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
            else {
                throw new AddException("Request already exists");
            }
        }
        else {
            throw new FindException("Couldn't find the user with the specified id");
        }

    }

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

    private Long maximReqId() {
        Long maxID = 0L;
        for(FriendRequest friendRequest: repoRequests.findAll()){
            if(friendRequest.getId() > maxID)
                maxID = friendRequest.getId();
        }
        return maxID;
    }
}
