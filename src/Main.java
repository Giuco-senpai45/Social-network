import domain.*;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import repository.Repository;
import repository.database.ChatDatabase;
import repository.database.FriendshipDatabase;
import repository.database.MessageDatabase;
import repository.database.UserDatabase;
import repository.file.FriendshipFile;
import repository.file.UserFile;
import ui.UI;

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
        Repository<Long, Message> repoMessage = new MessageDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres");
        Repository<Long, Chat> repoChat = new ChatDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres");
        //System.out.println(repoChat.findOne(1L));
        UI ui = new UI(repoUser,repoFriends, repoMessage, repoChat);
        ui.run();
    }
}
