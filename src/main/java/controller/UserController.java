package controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import main.domain.User;
import javafx.scene.input.MouseEvent;
import main.service.FriendRequestService;
import main.service.UserService;
import sn.socialnetwork.MainApp;

import java.io.IOException;

public class UserController {
    @FXML
    private Label userNameLabel;

    @FXML
    private AnchorPane profileAnchor;

    @FXML
    private AnchorPane friendRequestsAnchor;

    @FXML
    private StackPane stackProfile;

    @FXML
    private Pane changingPane;

    private FriendRequestsControll friendRequestsControll;
    private User loggedUser;
    private UserService userService;
    private FriendRequestService friendRequestService;

    public void loadAppLoggedUser(UserService userService, FriendRequestService friendRequestService,User user) {
        userNameLabel.setText(user.getLastName() + user.getFirstName());
        this.loggedUser = user;
        this.userService = userService;
        this.friendRequestService = friendRequestService;

    }

    public void profileClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/user-profile-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            UserProfileController userProfileController = fxmlLoader.getController();
            userProfileController.createUserProfile(userService,loggedUser);
        }
        catch(IOException e) {
                e.printStackTrace();
        }
    }

    public void friendrequestClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/friend-requests-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            FriendRequestsControll friendRequestsControll = fxmlLoader.getController();
            friendRequestsControll.initialise(userService,friendRequestService,loggedUser);
            friendRequestsControll.showCurrentFriendRequests();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}