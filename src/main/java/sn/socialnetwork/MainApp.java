package sn.socialnetwork;

import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import main.domain.*;
import main.domain.validators.*;
import main.repository.Repository;
import main.repository.database.*;
import main.repository.paging.PagingRepository;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.MessageService;
import main.service.UserService;

import java.io.IOException;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(MainApp.class.getResource("/views/login-view.fxml"));

        PagingRepository<Long, User> repoUser = new UserDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new UserValidator());
        PagingRepository<Tuple<Long,Long>, Friendship> repoFriends = new FriendshipDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendshipValidator());
        PagingRepository<Long, Message> repoMessage = new MessageDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new MessageValidator());
        PagingRepository<Long, Chat> repoChat = new ChatDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new ChatValidator());
        PagingRepository<Long, FriendRequest> repoRequests = new FriendRequestDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres", new FriendRequestValidator());
        PagingRepository<String, Login> repoLogin = new LoginDatabase("jdbc:postgresql://localhost:5432/social","postgres","postgres");
        UserService userService = new UserService(repoUser, repoFriends, repoLogin);
        FriendshipService friendsService = new FriendshipService(repoFriends, repoUser);
        MessageService messageService=new MessageService(repoFriends, repoUser, repoMessage, repoChat,"jdbc:postgresql://localhost:5432/social","postgres","postgres");
        FriendRequestService friendRequestService = new FriendRequestService(repoFriends,repoUser,repoRequests);;

        BorderPane loginLayout = loginLoader.load();
        LoginController loginController =  loginLoader.getController();
        loginController.setServicesLogin(userService, friendsService, friendRequestService,messageService);

        Iterable<Chat> chats = repoChat.findAll();
        chats.forEach(System.out::println);

        primaryStage.setScene(new Scene(loginLayout));
        primaryStage.setTitle("Truth Rose");
        primaryStage.getIcons().add(new Image("imgs/app_icon.png"));
        primaryStage.show();
        primaryStage.setWidth(800);
    }
}
