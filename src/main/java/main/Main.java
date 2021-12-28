package main;

import main.domain.*;
import main.domain.validators.*;
import main.repository.Repository;
import main.repository.database.*;
import main.repository.paging.PagingRepository;
import main.ui.UI;

/**
 * This is the main class
 */
public class Main {

    /**
     * This function runs the application
     * @param args arguments from the command line
     */
    public static void main(String[] args) {
        PagingRepository<Long,User> repoUser = new UserDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new UserValidator());
        PagingRepository<Tuple<Long,Long>, Friendship> repoFriends = new FriendshipDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendshipValidator());
        PagingRepository<Long, Message> repoMessage = new MessageDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new MessageValidator());
        PagingRepository<Long, Chat> repoChat = new ChatDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new ChatValidator());
        PagingRepository<Long, FriendRequest> repoRequests = new FriendRequestDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendRequestValidator());
        PagingRepository<String, Login> loginRepository = new LoginDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres");
        UI ui = new UI(repoUser,repoFriends, repoMessage, repoChat,repoRequests, loginRepository);
        ui.run();


    }
}
