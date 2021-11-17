import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import repository.Repository;
import repository.database.FriendshipDatabase;
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
        UI ui = new UI(repoUser,repoFriends);
        ui.run();
    }
}
