package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Callback;
import main.domain.*;
import main.service.*;
import main.service.serviceExceptions.FindException;
import sn.socialnetwork.MainApp;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;


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

    @FXML
    private GridPane postsPane;

    private User loggedUser;
    private User currentUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private PostService postService;


    public void initUserProfileController(UserService userService, User loggedUser, User currentUser, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService, PostService postService, Pane changingPane){
        this.changingPane = changingPane;
        root.setLayoutX(changingPane.getLayoutX());
        root.setLayoutY(changingPane.getLayoutY());
        this.loggedUser = loggedUser;
        this.currentUser = currentUser;
        this.userService = userService;
        this.messageService = messageService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.postService = postService;
        setUserProfile();
    }

    private void setUserProfile(){
        profileNameLabel.setText(null);
        profileNameLabel.setText(currentUser.getLastName() + "  "  + currentUser.getFirstName());
        setInfo();
        setPosts();
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
            userProfileController.initUserProfileController(userService, loggedUser, newUser, friendshipService, friendRequestService, messageService, postService, changingPane);
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
            if(messageService.testIfChatEmpty(loggedUser.getId(), chatId))
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
        Set<Post> posts = postService.getPostsOnPage(pageIndex, currentUser.getId());
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
        postService.setPageSize(3);
        int pageNumber = postService.numberOfPagesForPosts(currentUser.getId());
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
            Pagination pagination = new Pagination(pageNumber, 0);
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
}
