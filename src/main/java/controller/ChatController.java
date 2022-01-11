package controller;

import controller.pages.PageObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import main.domain.*;
import main.service.MasterService;
import main.utils.Observer;
import main.utils.events.MessageEvent;
import sn.socialnetwork.MainApp;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private TableColumn<Chat, String> userChats;

    @FXML
    private ComboBox chatMenu;

    @FXML
    private VBox conversationPane;

    @FXML
    private ScrollPane scroller;

    @FXML
    private TextField textFieldGroupName;

    @FXML
    private Label replyLabel;

    @FXML
    private AnchorPane closeReply;

    private PageObject pageObject;
    private Chat currentSelectedChat;
    private LocalDateTime currentMessageDate;
    private Long messageToReplyID;

    public void setServicesChat(PageObject pageObject){
        this.pageObject = pageObject;
        this.pageObject.getService().getMessageService().addObserver(this);
        currentMessageDate = null;
        scroller.setContent(conversationPane);
    }

    public void initChatView(Long chatID){
        replyLabel.setVisible(false);
        closeReply.setVisible(false);
        Image img = new Image("imgs/rosar.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(50);
        view.setPreserveRatio(true);
        sendMessageButton.setGraphic(view);
        ObservableList<String> choicesList = FXCollections.observableArrayList("Change group picture","Change group name","Create a new group chat");
        chatMenu.setItems(choicesList);
        chatMenu.getSelectionModel().selectedItemProperty().addListener((x,y,z)-> handleClickChatMenu());
        //TODO: deselecteaza optiunile dupa ce sunt apasate pls puiiii <3
        textMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    sendMessageAction(new ActionEvent());
                }
            }
        });
        start(chatID);
    }

    public void start(Long chatID){
        System.out.println("aici");
        displayChatsForUser(chatID);
        currentSelectedChat = pageObject.getService().getMessageService().getChatsForUser(pageObject.getLoggedUser().getId()).get(0);
        chatsList.getSelectionModel().selectFirst();            //in the beginning we select the first chat
        displayCurrentChat(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
    }

    private void displayChatsForUser(Long chatID){
        System.out.println("aici2");
        chatsList.getColumns().clear();
        if(chatID == -1)
            chatsList.setItems(FXCollections.observableArrayList(pageObject.getService().getMessageService().getChatsForUser(pageObject.getLoggedUser().getId())));
        else
            chatsList.setItems(FXCollections.observableArrayList(pageObject.getService().getMessageService().getAllChatsForUser(pageObject.getLoggedUser().getId(), chatID)));
        System.out.println("aici3");
        addChatImages();
    }

    private void addChatImages(){
        System.out.println("aici4");
        userChats.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Chat, String> call(TableColumn<Chat, String> param) {
                ImageView imageView = new ImageView();
                Circle circle = new Circle(40);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                TableCell<Chat, String> cell = new TableCell<>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            Image image = new Image(item, false);
                            imageView.setImage(image);
                            circle.setFill(new ImagePattern(image));
                            setCursor(Cursor.HAND);
                            setGraphic(circle);
                            setOnMouseClicked(ChatController.this::displayCurrentChat);
                            setAlignment(Pos.CENTER);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });
        userChats.setCellValueFactory(new PropertyValueFactory<>("url"));
        chatsList.getColumns().add(userChats);
        System.out.println("aici5");
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
                pageObject.getService().getMessageService().updateChat(updatedChat);
                displayChatsForUser(-1L);
            }
        }
        else if(option.equals("Create a new group chat")) {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/friends-groupchat.fxml"));
            Stage stage = new Stage();
            Scene scene = null;
            final Chat[] createdChat = {null};
            try {
                scene = new Scene(fxmlLoader.load());
                FriendsGroupChatController friendsGroupChatController = fxmlLoader.getController();
                friendsGroupChatController.setServices(pageObject);
                friendsGroupChatController.start();
                stage.setOnHidden(new EventHandler<>() {
                    @Override
                    public void handle(WindowEvent e) {
                        System.out.println("intru aiceeee");
                        createdChat[0] = friendsGroupChatController.getCreatedChat();
                        System.out.println(createdChat[0].getId());
                        if(pageObject.getService().getMessageService().testIfChatEmpty(pageObject.getLoggedUser().getId(), createdChat[0].getId()))
                            start(createdChat[0].getId());
                        else
                            start(-1L);
                    }
                });
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
            textFieldGroupName.setVisible(true);
            textFieldGroupName.getStyleClass().add("change-group-name");
            textFieldGroupName.setOnKeyPressed((KeyEvent event) -> {
                if(event.getCode() == KeyCode.ENTER){
                    if(!textFieldGroupName.getText().equals("")){
                        Chat updatedChat = new Chat();
                        updatedChat.setId(currentSelectedChat.getId());
                        updatedChat.setUrl(currentSelectedChat.getUrl());
                        updatedChat.setName(textFieldGroupName.getText());
                        pageObject.getService().getMessageService().updateChat(updatedChat);
                        textFieldGroupName.setText("");
                        textFieldGroupName.setVisible(false);
                        displayChatsForUser(-1L);
                        displayChat(updatedChat);
                    }
                    else {
                        textFieldGroupName.setVisible(false);
                    }
                }
            });
        }
    }

    private void updatePicsForPrivateChats(){
        for (Chat c : chatsList.getItems()) {
            Tuple<String,String> privateChat = pageObject.getService().getMessageService().getPrivateChatData(pageObject.getLoggedUser().getId(),c);
            if(privateChat!= null){
                c.setName(privateChat.getE1());
                c.setUrl(privateChat.getE2());
                pageObject.getService().getMessageService().updateChatForUser(c, pageObject.getLoggedUser().getId());
            }
        }
    }

    private void displayCurrentChat(MouseEvent event){
        System.out.println("aici6");
        conversationPane.getChildren().clear();
        currentSelectedChat = chatsList.getSelectionModel().getSelectedItem();
        displayChat(chatsList.getSelectionModel().getSelectedItem());
    }

    private void displayChat(Chat chat){
        System.out.println("aici7");
        conversationPane.getChildren().clear();
        currentSelectedChat = chat;
        updatePicsForPrivateChats();
        chatNameLabel.setText(currentSelectedChat.getName());
        List<ChatDTO> chatMessages = pageObject.getService().getMessageService().getConversation(currentSelectedChat.getId());
        System.out.println("aici8");
        if(chatMessages.size() == 0)
            showEmptyChat(chat);
        else
            for(ChatDTO chatDTO: chatMessages){
                showMessages(chatDTO);
            }
        System.out.println("aici9");
    }

    private void showMessages(ChatDTO chatDTO){
        LocalDateTime localDate = chatDTO.getTimestamp().toLocalDateTime();
        int day = localDate.getDayOfMonth();
        if(currentMessageDate == null || (day != currentMessageDate.getDayOfMonth())){
            currentMessageDate = chatDTO.getTimestamp().toLocalDateTime();
            Label date = new Label(currentMessageDate.getDayOfMonth() + " " + currentMessageDate.getMonth() + " " + currentMessageDate.getYear());
            VBox showDate = new VBox();
            showDate.getChildren().add(date);
            showDate.setAlignment(Pos.CENTER);
            conversationPane.getChildren().add(showDate);
        }

        User sender = pageObject.getService().getUserService().findUserById(chatDTO.getUserID());
        VBox photoAndHour = new VBox();
        Circle circle = new Circle();
        circle.setRadius(20);
        Image image = new Image(sender.getImageURL(), false);
        circle.setFill(new ImagePattern(image));
        LocalDateTime localDateTime = chatDTO.getTimestamp().toLocalDateTime();
        String hourText = ((localDateTime.getHour() > 9) ? String.valueOf(localDateTime.getHour()) : ("0" + localDateTime.getHour()));
        String minuteText = ((localDateTime.getMinute() > 9) ? String.valueOf(localDateTime.getMinute()) : ("0" + localDateTime.getMinute()));
        Label hour = new Label(hourText + ":" + minuteText);
        VBox.setMargin(hour, new Insets(0, 0, 0, 3));
        photoAndHour.getChildren().addAll(circle, hour);

        HBox icons = new HBox();
        ImageView imageView1 = new ImageView();
        {
            imageView1.setFitWidth(20);
            imageView1.setFitHeight(20);
            imageView1.setImage(new Image("/imgs/bin.png"));
            imageView1.setStyle("-fx-cursor: hand;");
            imageView1.setOnMouseClicked(event -> {
                Long messageID = chatDTO.getMessageID();
                pageObject.getService().getMessageService().deleteMessage(messageID);
            });
        }

        ImageView imageView2 = new ImageView();
        {
            imageView2.setFitWidth(20);
            imageView2.setFitHeight(20);
            imageView2.setImage(new Image("/imgs/reply.png"));
            imageView2.setStyle("-fx-cursor: hand;");
            imageView2.setOnMouseClicked(event -> {
                messageToReplyID = chatDTO.getMessageID();
                replyLabel.setText("Reply to " + messageToReplyID + ": " + chatDTO.getMessage());
                replyLabel.setVisible(true);
                closeReply.setVisible(true);
                closeReply.setOnMouseClicked(event1 -> {
                    replyLabel.setVisible(false);
                    closeReply.setVisible(false);
                });
            });
        }
        HBox.setMargin(imageView1, new Insets(0, 3, 0, 5));
        HBox.setMargin(imageView2, new Insets(0, 5, 0, 3));
        icons.getChildren().addAll(imageView1, imageView2);
        icons.setVisible(false);
        icons.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                icons.setVisible(true);
            }
        });
        icons.onMouseExitedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                icons.setVisible(false);
            }
        });

        HBox convo = new HBox();
        Label label = new Label(chatDTO.getMessage());
        {
            label.setPrefWidth(250);
            label.setPadding(new Insets(3, 5, 3, 5));
            label.setWrapText(true);
            label.setStyle("-fx-background-color: ddbea9; -fx-cursor: hand; -fx-background-radius: 15px;");
            label.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    icons.setVisible(true);
                }
            });
            label.onMouseExitedProperty().set(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    icons.setVisible(false);
                }
            });
        }
        Text text = new Text();
        text.setText(chatDTO.getMessage());
        if(text.getLayoutBounds().getWidth() < 250)
            label.setPrefWidth(text.getLayoutBounds().getWidth() + 10);

        VBox messageAndReply = new VBox();
        if(chatDTO.getReplyID() != -1){
            Message repliedTo = pageObject.getService().getMessageService().findOneMessage(chatDTO.getReplyID());
            Label replied = new Label("Reply to: " + repliedTo.getMessage());
            replied.setPrefWidth(240);
            replied.setPadding(new Insets(3, 5, 3, 5));
            replied.setWrapText(true);
            replied.setStyle("-fx-background-color: #ffe8d6; -fx-text-fill: #6b705c; -fx-background-radius: 15px;");
            replied.setFont(Font.font("System", FontPosture.ITALIC, 12));
            text.setText("Reply to: " + repliedTo.getMessage());
            if(text.getLayoutBounds().getWidth() < 240)
                replied.setPrefWidth(text.getLayoutBounds().getWidth() + 10);
            VBox.setMargin(replied, new Insets(0, 0, 0, 3));
            messageAndReply.getChildren().addAll(replied, label);
            HBox.setMargin(messageAndReply, new Insets(5, 0, 0, 0));
        }
        else {
            HBox.setMargin(messageAndReply, new Insets(15, 0, 0, 0));
            messageAndReply.getChildren().add(label);
        }

        HBox.setMargin(icons, new Insets(15, 0, 0, 0));
        if(Objects.equals(chatDTO.getUserID(), pageObject.getLoggedUser().getId())) {
            convo.getChildren().addAll(icons, messageAndReply, photoAndHour);
            convo.setAlignment(Pos.CENTER_RIGHT);
        }
        else{
            convo.getChildren().addAll(photoAndHour, messageAndReply, icons);
        }
        conversationPane.getChildren().add(convo);
        conversationPane.heightProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) {

                scroller.setVvalue((Double)newValue );
            }
        });
    }

    private void showEmptyChat(Chat chat){
        VBox emptyChatMessages = new VBox();
        Label firstMessage = new Label("Start this conversation!");
        Label secondMessage = new Label("Say hi to your friend!");
        firstMessage.setFont(Font.font(26));
        secondMessage.setFont(Font.font(22));
        VBox.setMargin(firstMessage, new Insets(30, 0, 0, 0));
        emptyChatMessages.getChildren().addAll(firstMessage, secondMessage);

        HBox usersPhoto = new HBox();
        if(chat.getChatUsers().size() == 2){
            Long id1 = chat.getChatUsers().get(0);
            Circle circle1 = new Circle();
            circle1.setRadius(40);
            Image image1 = new Image(pageObject.getService().getUserService().findUserById(id1).getImageURL(), false);
            circle1.setFill(new ImagePattern(image1));

            Long id2 = chat.getChatUsers().get(1);
            Circle circle2 = new Circle();
            circle2.setRadius(40);
            Image image2 = new Image(pageObject.getService().getUserService().findUserById(id2).getImageURL(), false);
            circle2.setFill(new ImagePattern(image2));
            usersPhoto.getChildren().addAll(circle1, circle2);
        }
        else{
            Circle circle = new Circle();
            circle.setRadius(40);
            Image image1 = new Image("/imgs/rose3.jpg", false);
            circle.setFill(new ImagePattern(image1));
            usersPhoto.getChildren().add(circle);
        }

        usersPhoto.setAlignment(Pos.CENTER);
        emptyChatMessages.setAlignment(Pos.CENTER);
        VBox emptyChat = new VBox();
        emptyChat.getChildren().addAll(emptyChatMessages, usersPhoto);
        conversationPane.getChildren().add(emptyChat);
    }

    public void sendMessageAction(ActionEvent actionEvent) {
        if(!textMessage.getText().isBlank()){
            if(replyLabel.isVisible()) {
                pageObject.getService().getMessageService().replyMessage(pageObject.getLoggedUser().getId(), textMessage.getText(), messageToReplyID);
                replyLabel.setVisible(false);
                closeReply.setVisible(false);
            }
            else
                pageObject.getService().getMessageService().sendMessage(pageObject.getLoggedUser().getId(), textMessage.getText(),currentSelectedChat.getChatUsers());
            textMessage.setText(null);
        }
    }

    public void selectChatById(Long chatId) {
        for(Chat c : chatsList.getItems()){
            if(c.getId().equals(chatId)){
                displayChat(c);
            }
        }
    }

    @Override
    public void update(MessageEvent messageEvent) {
        displayChat(currentSelectedChat);
//        displayCurrentChat(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
//                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
//                true, true, true, true, true, true, null));
    }
}
