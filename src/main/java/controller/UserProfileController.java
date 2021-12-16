package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import main.domain.*;
import main.service.FriendRequestService;
import main.service.FriendshipService;
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
    private ListView friendsList;

    @FXML
    private Pane changingPane;

    @FXML
    private Label friendshipStatus;

    @FXML
    private Button friendshipButton;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Label birthDateCompleted;

    @FXML
    private Label addressCompleted;

    @FXML
    private Label contactCompleted;


    private User loggedUser;
    private User currentUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;


    public void initUserProfileController(UserService userService, User loggedUser, User currentUser, FriendshipService friendshipService, FriendRequestService friendRequestService, Pane changingPane){
        this.loggedUser = loggedUser;
        this.currentUser = currentUser;
        this.userService = userService;
        this.changingPane = changingPane;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        setUserProfile();
    }

    private void setUserProfile(){
        profileNameLabel.setText(null);
        profileNameLabel.setText(currentUser.getLastName() + "  "  + currentUser.getFirstName());
        addressCompleted.setText(loggedUser.getAddress());
        birthDateCompleted.setText(loggedUser.getBirthDate().toString());
        contactCompleted.setText(loggedUser.getEmail());
        friendsList.setItems(FXCollections.observableArrayList(userService.getUserFriendList(currentUser.getId())));
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
                if (fr.getFrom() == loggedUser.getId()) {
                    found = true;
                    break;
                }
            if (found == true){
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

    @FXML
    public void handleFriendsListClick(MouseEvent mouseEvent) {
        int index = friendsList.getSelectionModel().getSelectedIndex();
        try {
            UserFriendshipsDTO userFriendshipsDTO = userService.getUserFriendList(currentUser.getId()).get(index);
            User newUser = userService.findUserById(userFriendshipsDTO.getFriendID());
            setNewUserProfile(newUser);
        }
        catch (IndexOutOfBoundsException e){

        }
    }

    public void setNewUserProfile(User newUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/user-profile-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            UserProfileController userProfileController = fxmlLoader.getController();
            userProfileController.initUserProfileController(userService, loggedUser, newUser, friendshipService, friendRequestService, changingPane);
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
            friendsList.setItems(FXCollections.observableArrayList(userService.getUserFriendList(currentUser.getId())));
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
    }


}
