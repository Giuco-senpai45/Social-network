package sn.socialnetwork;

import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.domain.*;
import main.domain.validators.*;
import main.repository.database.*;
import main.repository.paging.PagingRepository;
import main.service.*;

import java.io.IOException;
import java.time.LocalDateTime;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //TODO page ✔
        //TODO repara friend requesturile ✔
        //TODO generare rapoarte ✔ + catch errors ✖
        //TODO pagination la friend list ✔, search list ✔, friend requests, chat ?
        //TODO logout + fereastra de are you sure ✔
        //TODO update profile picture
        //TODO adaugare nume la chat
        //TODO schimbare culoare text dreapta sus ✔
        //TODO realiniere reply cu mesaj
        MasterService masterService = setMasterService();
        FXMLLoader loginLoader = new FXMLLoader(MainApp.class.getResource("/views/login-view.fxml"));
        BorderPane loginLayout = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.setServiceLogin(masterService);

        primaryStage.setScene(new Scene(loginLayout));
        primaryStage.setTitle("Truth Rose");
        primaryStage.getIcons().add(new Image("imgs/app_icon.png"));
        primaryStage.show();
        primaryStage.setWidth(800);
    }

    private MasterService setMasterService(){
        PagingRepository<Long, User> repoUser = new UserDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new UserValidator());
        PagingRepository<Tuple<Long,Long>, Friendship> repoFriends = new FriendshipDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendshipValidator());
        PagingRepository<Long, Message> repoMessage = new MessageDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new MessageValidator());
        PagingRepository<Long, Chat> repoChat = new ChatDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new ChatValidator());
        PagingRepository<Long, FriendRequest> repoRequests = new FriendRequestDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendRequestValidator());
        PagingRepository<String, Login> repoLogin = new LoginDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres");
        PagingRepository<Long, Post> repoPost = new PostDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres");
        PagingRepository<Long, RoseEvent> repoEvents = new RoseEventsDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres");

        UserService userService = new UserService(repoUser, repoFriends, repoLogin);
        FriendshipService friendsService = new FriendshipService(repoFriends, repoUser);
        MessageService messageService=new MessageService(repoFriends, repoUser, repoMessage, repoChat,"jdbc:postgresql://localhost:5432/social","postgres","postgres");
        FriendRequestService friendRequestService = new FriendRequestService(repoFriends,repoUser,repoRequests);;
        PostService postService = new PostService(repoUser, repoPost);
        RoseEventService eventService = new RoseEventService(repoUser,repoEvents);

        MasterService masterService = new MasterService(userService, friendsService, friendRequestService, messageService, postService, eventService);
        return masterService;
    }
}
