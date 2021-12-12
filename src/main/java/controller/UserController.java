package controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import main.domain.Friendship;
import main.domain.Tuple;
import main.domain.User;
import javafx.scene.input.MouseEvent;
import main.domain.UserFriendshipsDTO;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.UserService;
import sn.socialnetwork.MainApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private ListView searchList;

    private FriendRequestsControll friendRequestsControll;
    private User loggedUser;
    private UserService userService;
    private FriendRequestService friendRequestService;
    private FriendshipService friendshipService;

    public void loadAppLoggedUser(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService,User user) {
        userNameLabel.setText(user.getLastName() + user.getFirstName());
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.loggedUser = user;
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);
        searchBar.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                if(newValue && firstTime.get()){
                    profileAnchor.requestFocus(); // Delegate the focus to container
                    firstTime.setValue(false); // Variable value changed for future references
                }
            }
        });
    }

    public void profileClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/user-profile-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            UserProfileController userProfileController = fxmlLoader.getController();
            userProfileController.initUserProfileController(userService, loggedUser, loggedUser, friendshipService, friendRequestService, changingPane);
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

    @FXML
    public void handleChangeSearchBar(KeyEvent keyEvent) {
        if(Objects.equals(searchBar.getText(), "")) {
            searchList.setVisible(false);
//            final BooleanProperty firstTime = new SimpleBooleanProperty(true);
//            searchBar.focusedProperty().addListener((o, oldValue, newValue) -> {
//                if (newValue && firstTime.get()) {
//                    profileAnchor.requestFocus(); // Delegate the focus to container
//                    firstTime.setValue(false); // Variable value changed for future references
//                }
//            });
//            searchBar.setPromptText("Search user");
        }
        else {
            List<Tuple<String, Long>> tupleList = userService.allUsersByCharacters(searchBar.getText());
            List<String> foundList = new ArrayList<>();
            foundList = tupleList.stream()
                    .map(Tuple::getE1)
                    .collect(Collectors.toList());
            searchList.setItems(FXCollections.observableArrayList(foundList));
            searchList.setVisible(true);
        }
    }

    @FXML
    public void handleSearchListClick(MouseEvent mouseEvent) {
        int index = searchList.getSelectionModel().getSelectedIndex();
        try {
            Tuple<String, Long> tupleList = userService.allUsersByCharacters(searchBar.getText()).get(index);
            User newUser = userService.findUserById(tupleList.getE2());
            setSearchedUserProfile(newUser);
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void setSearchedUserProfile(User newUser) {
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

        searchBar.alignmentProperty();
    }
}