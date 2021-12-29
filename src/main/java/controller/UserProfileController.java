package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import main.domain.*;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.MessageService;
import main.service.UserService;
import main.service.serviceExceptions.FindException;
import sn.socialnetwork.MainApp;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class UserProfileController {

    @FXML
    private Label profileNameLabel;

    @FXML
    private Pane changingPane;

    @FXML
    private Label friendshipStatus;

    @FXML
    private Button friendshipButton;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Label addressCompleted;

    @FXML
    private Label dateCompleted;

    @FXML
    private Label studiesCompleted;

    @FXML
    private Label emailCompleted;

    @FXML
    private Label relationshipCompleted;

    @FXML
    private Label funFactCompleted;

    @FXML
    private AnchorPane root;

    @FXML
    private Circle circleAvatar;


    private User loggedUser;
    private User currentUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;


    public void initUserProfileController(UserService userService, User loggedUser, User currentUser, FriendshipService friendshipService, FriendRequestService friendRequestService,MessageService messageService, Pane changingPane){
        this.loggedUser = loggedUser;
        this.currentUser = currentUser;
        this.userService = userService;
        this.messageService = messageService;
        this.changingPane = changingPane;
        root.setLayoutX(changingPane.getLayoutX());
        root.setLayoutY(changingPane.getLayoutY());
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        setUserProfile();
    }

    private void setUserProfile(){
        profileNameLabel.setText(null);
        profileNameLabel.setText(currentUser.getLastName() + "  "  + currentUser.getFirstName());
        setInfo();
        if(Objects.equals(currentUser.getId(), loggedUser.getId())) {
            friendshipStatus.setVisible(false);
            sendMessageButton.setVisible(false);
            friendshipButton.setVisible(false);
        }
        else{
            sendMessageButton.setVisible(true);
            List<FriendRequest> friendRequests = friendRequestService.findPendingRequestsForUser(currentUser.getId());
            boolean found = false;
            for(FriendRequest fr: friendRequests)
                if (Objects.equals(fr.getFrom(), loggedUser.getId())) {
                    found = true;
                    break;
                }
            if (found){
                friendshipStatus.setVisible(false);
                friendshipButton.setText("Cancel friend request");
                friendshipButton.setVisible(true);
            }
            else {
                try {
                    Friendship friendship = friendshipService.findFriendshipById(new Tuple(loggedUser.getId(), currentUser.getId()));
                    friendshipStatus.setText("Friends since " + friendship.getDate());
                    friendshipStatus.setVisible(true);
                    friendshipButton.setText("Remove friend");
                    friendshipButton.setVisible(true);
                } catch (FindException e) {
                    friendshipStatus.setVisible(false);
                    friendshipButton.setText("Add new friend");
                    friendshipButton.setVisible(true);
                }
            }
        }
    }

    private void setInfo(){
        circleAvatar.setStroke(Color.BLACK);
        Image image = new Image(currentUser.getImageURL(), false);
        circleAvatar.setFill(new ImagePattern(image));

        addressCompleted.setText(currentUser.getAddress());
        dateCompleted.setText(currentUser.getBirthDate().toString());
        studiesCompleted.setText(currentUser.getLastGraduatedSchool());
        emailCompleted.setText(currentUser.getEmail());
        relationshipCompleted.setText(currentUser.getRelationshipStatus());
        funFactCompleted.setText(currentUser.getFunFact());
    }

    public void setNewUserProfile(User newUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/user-profile-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            UserProfileController userProfileController = fxmlLoader.getController();
            userProfileController.initUserProfileController(userService, loggedUser, newUser, friendshipService, friendRequestService, messageService, changingPane);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleFriendshipButton(ActionEvent actionEvent) {
        String text = friendshipButton.getText();
        if (Objects.equals(text, "Remove friend")){
            friendshipService.removeFriendship(new Tuple<>(loggedUser.getId(), currentUser.getId()));
//            friendsList.setItems(FXCollections.observableArrayList(userService.getUserFriendList(currentUser.getId())));
            friendshipStatus.setVisible(false);
            friendshipButton.setText("Add new friend");
        }
        else if (Objects.equals(text, "Add new friend")){
            friendRequestService.sendRequest(loggedUser.getId(), currentUser.getId());
            friendshipStatus.setVisible(false);
            friendshipButton.setText("Cancel friend request");
        }
        else if (Objects.equals(text, "Cancel friend request")){
            friendRequestService.deleteFriendRequest(loggedUser.getId(), currentUser.getId());
            friendshipStatus.setVisible(false);
            friendshipButton.setText("Add new friend");
        }
    }

    @FXML
    public void handleMessagesButton(ActionEvent actionEvent) {
        Long chatId =  messageService.createPrivateChatWithUser(loggedUser.getId(), currentUser.getId());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/chat-view.fxml"));
            if (changingPane.getChildren() != null) {
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            ChatController chatController = fxmlLoader.getController();
            chatController.setServicesChat(messageService,userService,loggedUser,friendshipService);
            chatController.initChatView();
            chatController.selectChatById(chatId);
        }
        catch(IOException e) {
                e.printStackTrace();
        }
    }
}
