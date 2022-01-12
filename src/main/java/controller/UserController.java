package controller;

import controller.pages.PageObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
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
    private BorderPane userRoot;

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

    private Stage mainStage;
    private Integer searchListNumber;
    private Integer countIndex = 1;
    private int pageIndex = 1;
    private int leftLimit = 0;
    private PageObject pageObject;

    public void loadAppLoggedUser(PageObject pageObject, Stage stage) {
        this.pageObject = pageObject;
        this.mainStage = stage;
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

    private void setSearchList(){
        searchListNumber = pageObject.getService().getUserService().allUsersByCharacters(searchBar.getText(), -1).size();
        System.out.println(searchListNumber);
        int nr = leftLimit + 2;
        if(nr > searchListNumber)
            nr = searchListNumber;
        List<Tuple<String, Long>> tupleList = pageObject.getService().getUserService().allUsersByCharacters(searchBar.getText(), leftLimit);
        List<String> foundList = tupleList.stream()
                .map(Tuple::getE1)
                .collect(Collectors.toList());
        searchList.setItems(FXCollections.observableArrayList(foundList));
        searchList.setVisible(true);
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);
        searchBar.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                if(newValue && firstTime.get()){
                    searchList.requestFocus();
                    firstTime.setValue(false);
                }
            }
        });
    }

    @FXML
    public void handleChangeSearchBar(KeyEvent keyEvent) {
        if(Objects.equals(searchBar.getText(), "")) {
            leftLimit = 0;
            searchList.setVisible(false);
        }
        else {
            setSearchList();
        }
    }

    @FXML
    public void handleSearchListClick(MouseEvent mouseEvent) {
        int index = searchList.getSelectionModel().getSelectedIndex();
        try {
            Tuple<String, Long> tupleList = pageObject.getService().getUserService().allUsersByCharacters(searchBar.getText(), leftLimit).get(index);
            User newUser = pageObject.getService().getUserService().findUserById(tupleList.getE2());
            setSearchedUserProfile(newUser);
            searchList.setVisible(false);
            searchBar.setText(null);
            searchBar.setPromptText("Search user");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void handleScroll(ScrollEvent scrollEvent) {
        if(scrollEvent.getDeltaY()>0 && (leftLimit+2)<searchListNumber){ //scroll up
            leftLimit ++;
        }
        if(scrollEvent.getDeltaY()<0 && leftLimit > 0){ //scroll down and there are messages left
            leftLimit--;
        }
        setSearchList();
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.UP) && (leftLimit-2)>=-1){
            leftLimit--;
        }
        if(keyEvent.getCode().equals(KeyCode.DOWN) && leftLimit!=searchListNumber ){
            leftLimit++;
        }
        setSearchList();
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
            if (changingPane.getChildren() != null) {
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
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

    public void handleLogoutClicked(MouseEvent mouseEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/logout-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());

            LogoutController logoutController = fxmlLoader.getController();
            logoutController.init(mainStage, stage);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        stage.setScene(scene);
        stage.setTitle("Truth Rose");
        stage.setResizable(false);
        stage.getIcons().add(new Image("imgs/app_icon.png"));
        stage.show();
    }
}