package controller;

import controller.pages.PageObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import main.domain.*;
import javafx.scene.input.MouseEvent;
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
    private Pane changingPane;

    @FXML
    private TextField searchBar;

    @FXML
    private ListView searchList;

    @FXML
    private Label usernameLabel;

    @FXML
    private ImageView report_image;
    private PageObject pageObject;

    public void loadAppLoggedUser(PageObject pageObject) {
        this.pageObject = pageObject;
        User loggedUser = pageObject.getLoggedUser();

        Login login = null;
        for(Login l: pageObject.getService().getUserService().allRegisteredUsers())
            if(Objects.equals(l.getUserID(), loggedUser.getId()))
                login = l;
        usernameLabel.setText("Truth Rose | @" + login.getId());

        loadUserProfile();
        getFocusFromFirstTextField();
    }

    private void getFocusFromFirstTextField(){
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);
        searchBar.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                if(newValue && firstTime.get()){
                    profileAnchor.requestFocus();
                    firstTime.setValue(false);
                }
            }
        });
    }


    private void loadUserProfile(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/user-profile-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            UserProfileController userProfileController = fxmlLoader.getController();
            userProfileController.initUserProfileController(pageObject, pageObject.getLoggedUser(), changingPane);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void profileClicked(MouseEvent mouseEvent) {
        loadUserProfile();
    }

    public void friendrequestClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/friend-requests-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            FriendRequestsControll friendRequestsControll = fxmlLoader.getController();
            friendRequestsControll.initialise(pageObject);
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
        }
        else {
            List<Tuple<String, Long>> tupleList = pageObject.getService().getUserService().allUsersByCharacters(searchBar.getText());
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
            Tuple<String, Long> tupleList = pageObject.getService().getUserService().allUsersByCharacters(searchBar.getText()).get(index);
            User newUser = pageObject.getService().getUserService().findUserById(tupleList.getE2());
            setSearchedUserProfile(newUser);
            searchList.setVisible(false);
            searchBar.setText(null);
            searchBar.setPromptText("Search user");
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
            userProfileController.initUserProfileController(pageObject, newUser, changingPane);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        searchBar.alignmentProperty();
    }

    public void friendsClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/friends-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            FriendsController friendsController = fxmlLoader.getController();
            friendsController.setController(pageObject);
            friendsController.start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void chatsClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/chat-view.fxml"));
            if(changingPane.getChildren() != null){
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            ChatController chatController = fxmlLoader.getController();
            chatController.setServicesChat(pageObject);
            chatController.initChatView(-1L);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void handleReportsImageClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/report-view.fxml"));
            ReportController reportController = fxmlLoader.getController();
            reportController.init(pageObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eventsClicked(MouseEvent mouseEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/events-view.fxml"));
            if (changingPane.getChildren() != null) {
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            EventsController eventsController = fxmlLoader.getController();
            eventsController.init(pageObject);
            eventsController.initEventsView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}