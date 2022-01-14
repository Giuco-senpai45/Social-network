package controller;

import controller.pages.PageObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Callback;
import main.domain.*;
import main.service.MasterService;
import main.service.serviceExceptions.RemoveException;
import main.utils.Observer;
import main.utils.events.FriendDeletionEvent;


public class FriendsController implements Observer<FriendDeletionEvent> {

    @FXML
    private AnchorPane root;

    @FXML
    private TableView friendsList;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> avatar;
    @FXML
    private TableColumn<UserFriendshipsDTO, String> fullName;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> friendshipDate;

    @FXML
    private TableColumn<UserFriendshipsDTO, Void> action;

    @FXML
    private Pagination friendsPage;

    private PageObject pageObject;
    private ObservableList<UserFriendshipsDTO> model = FXCollections.observableArrayList();

    public void setController(PageObject pageObject){
        this.pageObject = pageObject;
        this.pageObject.getService().getFriendshipService().addObserver(this);
    }

    public void start(){
        int pageNumber = pageObject.getService().getUserService().numberOfPagesForFriends(pageObject.getLoggedUser().getId());
        if(pageNumber == 0){
            Label noFriends = new Label("You don't have any friends. ☹ ");
            friendsList.setVisible(false);
            noFriends.setFont(Font.font(26));
            noFriends.setPadding(new Insets(150, 0, 0, 100));
            root.getChildren().add(noFriends);
        }
        else {
            root.getChildren().remove(friendsPage);
            friendsPage = new Pagination(pageNumber, 0);
            friendsPage.setPrefHeight(600);
            friendsPage.setPrefWidth(659);
            friendsPage.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer pageIndex) {
                    if (pageIndex >= pageNumber) {
                        return null;
                    } else {
                        return createPage(pageIndex);
                    }
                }
            });
            root.getChildren().add(friendsPage);
        }
    }

    private TableView createPage(int pageIndex){
        model.setAll(pageObject.getService().getUserService().getUserFriendList(pageObject.getLoggedUser().getId(), pageIndex, 4));
        addImageToTable();
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fullName.setStyle("-fx-alignment: CENTER; -fx-background-color: a5a58d;");
        avatar.prefWidthProperty().bind(friendsList.widthProperty().multiply(0.19));
        fullName.prefWidthProperty().bind(friendsList.widthProperty().multiply(0.40));
        friendshipDate.prefWidthProperty().bind(friendsList.widthProperty().multiply(0.18));
        action.prefWidthProperty().bind(friendsList.widthProperty().multiply(0.223));
        friendshipDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        friendshipDate.setStyle("-fx-alignment: CENTER; -fx-background-color: a5a58d;");
        addButtonToTable();
        friendsList.setItems(model);
        friendsList.setStyle("-fx-font-size: 16;");
        return friendsList;
    }

    private void addButtonToTable() {
        Callback<TableColumn<UserFriendshipsDTO, Void>, TableCell<UserFriendshipsDTO, Void>> cellFactory = new Callback<TableColumn<UserFriendshipsDTO, Void>, TableCell<UserFriendshipsDTO, Void>>() {
            @Override
            public TableCell<UserFriendshipsDTO, Void> call(final TableColumn<UserFriendshipsDTO, Void> param) {
                final TableCell<UserFriendshipsDTO, Void> cell = new TableCell<UserFriendshipsDTO, Void>() {

                    private final Button btn = new Button("Remove friend");

                    {
                        btn.getStyleClass().add("remove-friend-btn");
                        btn.setOnAction((ActionEvent event) -> {
                            UserFriendshipsDTO data = getTableView().getItems().get(getIndex());
                            try {
                                pageObject.getService().getFriendshipService().removeFriendship(new Tuple<>(pageObject.getLoggedUser().getId(), data.getFriendID()));
                                pageObject.getService().getFriendRequestService().deleteFriendRequest(pageObject.getLoggedUser().getId(), data.getFriendID());
                            }
                            catch(RemoveException e){
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setAlignment(Pos.CENTER);
                            setGraphic(btn);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        };

        action.setCellFactory(cellFactory);
       // friendsList.getColumns().add(action);
    }

    private void addImageToTable(){
        avatar.setCellFactory(new Callback<TableColumn<UserFriendshipsDTO, String>, TableCell<UserFriendshipsDTO, String>>() {
            @Override
            public TableCell<UserFriendshipsDTO, String> call(TableColumn<UserFriendshipsDTO, String> param) {
                ImageView imageView = new ImageView();
                Circle circle = new Circle(20);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
                TableCell<UserFriendshipsDTO, String> cell = new TableCell<>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            Image image = new Image(item, false);
                            imageView.setImage(image);
                            circle.setFill(new ImagePattern(image));
                            //setCursor(Cursor.HAND);
                            setGraphic(circle);
                            //setOnMouseClicked(ChatController.this::displayCurrentChat);
                            //setAlignment(Pos.CENTER);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        avatar.setCellValueFactory(new PropertyValueFactory<UserFriendshipsDTO, String>("imageURL"));
    }

    @Override
    public void update(FriendDeletionEvent friendDeletionEvent) {
        start();
    }
}
