package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import main.domain.*;
import main.service.MessageService;
import main.service.UserService;
import main.utils.Observer;
import main.utils.events.MessageEvent;

import java.util.List;

public class ChatController implements Observer<MessageEvent> {

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
    private User loggedUser;

    public void setServicesChat(MessageService messageService, UserService userService,User loggedUser){
        this.messageService = messageService;
        this.messageService.addObserver(this);
        this.userService = userService;
        this.loggedUser = loggedUser;
    }

    public void initChatView(){
        Image img = new Image("imgs/rosar.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(50);
        view.setPreserveRatio(true);
        sendMessageButton.setGraphic(view);
        ObservableList<String> choicesList = FXCollections.observableArrayList("Change group picture","Change group name");
        chatMenu.setItems(choicesList);
        start();
    }

    public void start(){
        chatsList.getColumns().clear();
        chatsList.setItems(FXCollections.observableArrayList(messageService.getChatsForUser(loggedUser.getId())));
        addChatImages();
        currentSelectedChat = messageService.getChatsForUser(loggedUser.getId()).get(0);
        chatsList.getSelectionModel().selectFirst();
        displayCurrentChat(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
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

    private void displayCurrentChat(MouseEvent event){
        currentSelectedChat = chatsList.getSelectionModel().getSelectedItem();
        Tuple<String,String> privateChat = messageService.getPrivateChatData(loggedUser.getId(),currentSelectedChat);
        if(privateChat!= null){
            chatNameLabel.setText(privateChat.getE1());
            currentSelectedChat.setUrl(privateChat.getE2());
            messageService.update(currentSelectedChat, loggedUser.getId());
        }
        else {
            chatNameLabel.setText(currentSelectedChat.getName());
        }
        List<ChatDTO> chatMessages = messageService.getConversation(currentSelectedChat.getId());
        messagesListView.setItems(FXCollections.observableArrayList(chatMessages));
    }

    public void sendMessageAction(ActionEvent actionEvent) {
        if(!textMessage.getText().isBlank()){
            currentSelectedChat.getChatUsers().forEach(System.out::println);
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
