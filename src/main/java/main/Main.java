package main;

import main.domain.*;
import main.domain.validators.*;
import main.repository.Repository;
import main.repository.database.*;
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
        Repository<Long,User> repoUser = new UserDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new UserValidator());
        Repository<Tuple<Long,Long>, Friendship> repoFriends = new FriendshipDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendshipValidator());
        Repository<Long, Message> repoMessage = new MessageDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new MessageValidator());
        Repository<Long, Chat> repoChat = new ChatDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new ChatValidator());
        Repository<Long, FriendRequest> repoRequests = new FriendRequestDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendRequestValidator());
        UI ui = new UI(repoUser,repoFriends, repoMessage, repoChat,repoRequests);
        ui.run();
    }
}
