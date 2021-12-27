package controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.domain.*;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.MessageService;
import main.service.UserService;
import main.utils.Observer;
import main.utils.events.MessageEvent;
import sn.socialnetwork.MainApp;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatController implements Observer<MessageEvent> {

    @FXML
    private BorderPane chatPane;

    @FXML
    private Button sendMessageButton;

    @FXML
    private TextArea textMessage;

    @FXML
    private TableView<Chat> chatsList;

    @FXML
    private Label chatNameLabel;

    @FXML
    private ListView<ChatDTO> messagesListView;

    @FXML
    private TableColumn<Chat, String> userChats;

    @FXML
    private ComboBox chatMenu;

    private Chat currentSelectedChat;
    private MessageService messageService;
    private UserService userService;
    private FriendshipService friendshipService;
    private User loggedUser;

    public void setServicesChat(MessageService messageService, UserService userService,User loggedUser,FriendshipService friendshipService){
        this.messageService = messageService;
        this.messageService.addObserver(this);
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.loggedUser = loggedUser;
    }

    public void initChatView(){
        Image img = new Image("imgs/rosar.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(50);
        view.setPreserveRatio(true);
        sendMessageButton.setGraphic(view);
        ObservableList<String> choicesList = FXCollections.observableArrayList("Change group picture","Change group name","Create a new group chat");
        chatMenu.setItems(choicesList);
        chatMenu.getSelectionModel().selectedItemProperty().addListener((x,y,z)-> handleClickChatMenu());
        textMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    sendMessageAction(new ActionEvent());
                }
            }
        });
        start();
    }

    public void start(){
        displayChatsForUser();
        currentSelectedChat = messageService.getChatsForUser(loggedUser.getId()).get(0);
        chatsList.getSelectionModel().selectFirst();            //in the beginning we select the first chat
        displayCurrentChat(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
    }

    private void displayChatsForUser(){
        chatsList.getColumns().clear();
        chatsList.setItems(FXCollections.observableArrayList(messageService.getChatsForUser(loggedUser.getId())));
        addChatImages();
    }

    private void addChatImages(){
        userChats.setCellFactory(new Callback<TableColumn<Chat, String>, TableCell<Chat, String>>() {
            @Override
            public TableCell<Chat, String> call(TableColumn<Chat, String> param) {
                ImageView imageView = new ImageView();
                Circle circle = new Circle(40);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                TableCell<Chat, String> cell =  new TableCell<Chat, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            Image image = new Image(item, false);
                            imageView.setImage(image);
                            circle.setFill(new ImagePattern(image));
                            setCursor(Cursor.HAND);
                            setGraphic(circle);
                            setOnMouseClicked((MouseEvent event) -> {
                                displayCurrentChat(event);
                            });
                            setAlignment(Pos.CENTER);
                        }
                        else{
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });
        userChats.setCellValueFactory(new PropertyValueFactory<Chat, String>("url"));
        chatsList.getColumns().add(userChats);
    }

    private void handleClickChatMenu(){
        String option = chatMenu.getSelectionModel().getSelectedItem().toString();
        if(option.equals("Change group picture")){
            FileChooser fileChooser = new FileChooser();
            Stage stage =(Stage) chatPane.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);
            if(file != null){
                Chat updatedChat = new Chat();
                updatedChat.setId(currentSelectedChat.getId());
                updatedChat.setUrl(file.getAbsolutePath());
                updatedChat.setName(currentSelectedChat.getName());
                messageService.updateChat(updatedChat);
                displayChatsForUser();
            }
        }
        else if(option.equals("Create a new group chat")) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/friends-groupchat.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
                FriendsGroupChatController friendsGroupChatController = fxmlLoader.getController();
                friendsGroupChatController.setServices(userService,friendshipService,loggedUser);
                friendsGroupChatController.start();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Creating group chat");
            stage.setScene(scene);
            stage.getIcons().add(new Image("imgs/app_icon.png"));
            stage.show();
        }
        else{
            System.out.println("Change group name");
            //TODO change chat name
        }
    }

    private void updatePicsForPrivateChats(){
        for (Chat c : chatsList.getItems()) {
            Tuple<String,String> privateChat = messageService.getPrivateChatData(loggedUser.getId(),c);
            if(privateChat!= null){
                c.setName(privateChat.getE1());
                c.setUrl(privateChat.getE2());
                messageService.updateChatForUser(c, loggedUser.getId());
            }
        }
    }

    private void displayCurrentChat(MouseEvent event){
        currentSelectedChat = chatsList.getSelectionModel().getSelectedItem();
        updatePicsForPrivateChats();
        chatNameLabel.setText(currentSelectedChat.getName());
        List<ChatDTO> chatMessages = messageService.getConversation(currentSelectedChat.getId());

        messagesListView.setItems(FXCollections.observableArrayList(chatMessages));
        messagesListView.setCellFactory(lv -> {
            ListCell<ChatDTO> cell = new ListCell<ChatDTO>() {

                private Label label = new Label();
                {
                    label.setWrapText(true);
                    label.maxWidthProperty().bind(Bindings.createDoubleBinding(
                            () -> getWidth() - getPadding().getLeft() - getPadding().getRight() - 1,
                            widthProperty(), paddingProperty()));
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }

                @Override
                protected void updateItem(ChatDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        label.setText(item.toString());
                        if(item.getUserID().equals(loggedUser.getId())){
                            label.setAlignment(Pos.CENTER_RIGHT);
                            label.setTranslateX(-5);
                            label.getStyleClass().clear();
                            label.getStyleClass().add("message-logged-user");
                        }
                        else {
                            label.setAlignment(Pos.CENTER_LEFT);
                        }
                        setGraphic(label);
                    }
                }
            };
            return cell ;
        });
    }

    public void sendMessageAction(ActionEvent actionEvent) {
        if(!textMessage.getText().isBlank()){
            messageService.sendMessage(loggedUser.getId(), textMessage.getText(),currentSelectedChat.getChatUsers());
            textMessage.setText(null);
        }
    }

    @Override
    public void update(MessageEvent messageEvent) {
        displayCurrentChat(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
    }
}
