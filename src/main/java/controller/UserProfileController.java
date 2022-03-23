package controller;

import controller.pages.PageObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.domain.*;
import main.service.serviceExceptions.FindException;
import main.utils.Observer;
import main.utils.events.UserEvent;
import sn.socialnetwork.MainApp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class UserProfileController implements Observer<UserEvent> {

    @FXML
    private AnchorPane root;

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
    private Circle circleAvatar;

    @FXML
    private GridPane postsPane;

    @FXML
    private Button updateProfileButton;

    @FXML
    private Button addPostButton;

    private Pagination pagination;

    private PageObject pageObject;
    private User currentUser;



    public void initUserProfileController(PageObject pageObject, User currentUser, Pane changingPane){
        this.pageObject = pageObject;
        this.changingPane = changingPane;
        this.pageObject.getService().getUserService().addObserver(this);
        root.setLayoutX(changingPane.getLayoutX());
        root.setLayoutY(changingPane.getLayoutY());
        this.currentUser = currentUser;
        setUserProfile();
    }

    private void setUserProfile(){
        profileNameLabel.setText(null);
        profileNameLabel.setText(currentUser.getLastName() + "  "  + currentUser.getFirstName());
        setInfo();
        setPosts();
        if(Objects.equals(currentUser.getId(), pageObject.getLoggedUser().getId())) {
            friendshipStatus.setVisible(false);
            sendMessageButton.setVisible(false);
            friendshipButton.setVisible(false);
            updateProfileButton.setVisible(true);
            addPostButton.setVisible(true);
        }
        else{
            updateProfileButton.setVisible(false);
            addPostButton.setVisible(false);
            sendMessageButton.setVisible(true);
            List<FriendRequest> friendRequests = pageObject.getService().getFriendRequestService().findPendingRequestsForUser(currentUser.getId());
            boolean found = false;
            for(FriendRequest fr: friendRequests)
                if (Objects.equals(fr.getFrom(), pageObject.getLoggedUser().getId())) {
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
                    Friendship friendship = pageObject.getService().getFriendshipService().findFriendshipById(new Tuple(pageObject.getLoggedUser().getId(), currentUser.getId()));
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
            userProfileController.initUserProfileController(pageObject, newUser, changingPane);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleFriendshipButton(ActionEvent actionEvent) {
        String text = friendshipButton.getText();
        if (Objects.equals(text, "Remove friend")){
            pageObject.getService().getFriendshipService().removeFriendship(new Tuple<>(pageObject.getLoggedUser().getId(), currentUser.getId()));
//            friendsList.setItems(FXCollections.observableArrayList(userService.getUserFriendList(currentUser.getId())));
            pageObject.getService().getFriendRequestService().deleteFriendRequest(pageObject.getLoggedUser().getId(), currentUser.getId());
            friendshipStatus.setVisible(false);
            friendshipButton.setText("Add new friend");
        }
        else if (Objects.equals(text, "Add new friend")){
            pageObject.getService().getFriendRequestService().sendRequest(pageObject.getLoggedUser().getId(), currentUser.getId());
            friendshipStatus.setVisible(false);
            friendshipButton.setText("Cancel friend request");
        }
        else if (Objects.equals(text, "Cancel friend request")){
            pageObject.getService().getFriendRequestService().deleteFriendRequest(pageObject.getLoggedUser().getId(), currentUser.getId());
            friendshipStatus.setVisible(false);
            friendshipButton.setText("Add new friend");
        }
    }

    @FXML
    public void handleMessagesButton(ActionEvent actionEvent) {
        Long chatId = pageObject.getService().getMessageService().createPrivateChatWithUser(pageObject.getLoggedUser().getId(), currentUser.getId());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/chat-view.fxml"));
            if (changingPane.getChildren() != null) {
                changingPane.getChildren().clear();
            }
            changingPane.getChildren().add(fxmlLoader.load());
            ChatController chatController = fxmlLoader.getController();
            chatController.setServicesChat(pageObject);
            if(pageObject.getService().getMessageService().testIfChatEmpty(pageObject.getLoggedUser().getId(), chatId))
                chatController.initChatView(chatId);
            else
                chatController.initChatView(-1L);
            chatController.selectChatById(chatId);
        }
        catch(IOException e) {
                e.printStackTrace();
        }
    }

    private GridPane createPage(Integer pageIndex){
        Set<Post> posts = pageObject.getService().getPostService().getPostsOnPage(pageIndex, currentUser.getId());
        GridPane postsPane = new GridPane();
        postsPane.setPrefHeight(250);
        postsPane.setPrefWidth(659);

        ImageView firstPost = new ImageView();
        firstPost.setFitWidth(200);
        firstPost.setFitHeight(200);
        firstPost.setPreserveRatio(true);

        ImageView secondPost = new ImageView();
        secondPost.setFitWidth(200);
        secondPost.setFitHeight(200);
        secondPost.setPreserveRatio(true);

        ImageView thirdPost = new ImageView();
        thirdPost.setFitWidth(200);
        thirdPost.setFitHeight(200);
        thirdPost.setPreserveRatio(true);

        int columnCount = 3;
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPrefWidth(220);
        cc.setHgrow(Priority.ALWAYS);
        cc.setHalignment(HPos.CENTER);
        for (int i = 0; i < columnCount; i++) {
            postsPane.getColumnConstraints().add(cc);
        }

        postsPane.add(firstPost, 0, 0, 1, 1);
        postsPane.add(secondPost, 1, 0, 1, 1);
        postsPane.add(thirdPost, 2, 0, 1, 1);
        GridPane.setMargin(firstPost, new Insets(15, 0, 0, 10));
        GridPane.setMargin(secondPost, new Insets(15, 0, 0, 10));
        GridPane.setMargin(thirdPost, new Insets(15, 0, 0, 10));

        int count = 0;
        for(Post post: posts){
            Image image = new Image(post.getPostURL());
            if(count == 0)
                firstPost.setImage(image);
            else if(count == 1)
                secondPost.setImage(image);
            else if(count == 2)
                thirdPost.setImage(image);
            count++;
        }
        return postsPane;
    }

    private void setPosts(){
        root.getChildren().remove(pagination);
        pageObject.getService().getPostService().setPageSize(3);
        int pageNumber = pageObject.getService().getPostService().numberOfPagesForPosts(currentUser.getId());
        if(pageNumber == 0){
            VBox emptyPage = new VBox();
            emptyPage.setLayoutY(270);
            emptyPage.setPrefWidth(659);
            AnchorPane emptyPosts = new AnchorPane();
            emptyPosts.setPrefHeight(260);
            emptyPosts.setPrefWidth(660);
            BackgroundImage myBI = new BackgroundImage(new Image("/imgs/roses_bkg.png", 660, 250, true, true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            emptyPosts.setBackground(new Background(myBI));
            Label label = new Label("This user doesn't have any posts!");
            label.setFont(Font.font(26));
            VBox.setMargin(label, new Insets(0, 0, 0, 150));
            emptyPage.getChildren().addAll(emptyPosts, label);
            root.getChildren().add(emptyPage);
        }
        else {
            pagination = new Pagination(pageNumber, 0);
            pagination.setLayoutY(270);
            BackgroundImage myBI = new BackgroundImage(new Image("/imgs/roses_bkg.png", 660, 260, false, true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            pagination.setBackground(new Background(myBI));
            pagination.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer pageIndex) {
                    if (pageIndex >= pageNumber) {
                        return null;
                    } else {
                        return createPage(pageIndex);
                    }
                }
            });
            root.getChildren().add(pagination);
        }
    }

    public void handleUpdateProfileClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) root.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            String url = file.getAbsolutePath();
            User user = pageObject.getLoggedUser();
            pageObject.getService().getUserService().updateUser(user.getId(), user.getFirstName(), user.getLastName(), user.getAddress(), user.getBirthDate(), user.getGender(), user.getEmail(), user.getLastGraduatedSchool(), user.getRelationshipStatus(), user.getFunFact(), url, user.getNotificationSubscription());
        }
    }

    public void handleAddPostClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) root.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            String url = file.getAbsolutePath();
            pageObject.getService().getPostService().addNewPost(url, pageObject.getLoggedUser().getId());
            setPosts();
        }
    }

    @Override
    public void update(UserEvent userEvent) {
        currentUser = pageObject.getService().getUserService().findUserById(currentUser.getId());
        setInfo();
    }
}
