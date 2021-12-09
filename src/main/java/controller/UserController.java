package controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.domain.User;
import javafx.scene.input.MouseEvent;
import sn.socialnetwork.MainApp;

import java.io.IOException;

public class UserController {

    @FXML
    private Label labelFirstName;

    @FXML
    private Label labelLastName;

    @FXML
    private Label userNameLabel;

    @FXML
    private AnchorPane profileAnchor;

    @FXML
    private AnchorPane friendRequestsAnchor;

    @FXML
    private Pane changingPanel;

    private FriendRequestsControll friendRequestsControll;

    public void createUserProfile(User user){
        userNameLabel.setText(user.getLastName() + user.getFirstName());
        EventHandler<Event> changeToFriendRequestsEvent = new EventHandler<Event>(){

            @Override
            public void handle(Event event) {
                loadFriendRequestspanel();
            }
        };
        friendRequestsAnchor.addEventHandler(MouseEvent.MOUSE_PRESSED, changeToFriendRequestsEvent);
        labelFirstName.setText(user.getFirstName());
        labelLastName.setText(user.getLastName());
    }

    private void loadFriendRequestspanel(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/friend-requests-view.fxml"));
            changingPanel.getChildren().add(fxmlLoader.load());
            friendRequestsControll = fxmlLoader.getController();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}