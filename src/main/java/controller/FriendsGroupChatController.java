package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import main.domain.FriendRequest;
import main.domain.Tuple;
import main.domain.User;
import main.domain.UserFriendshipsDTO;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.UserService;
import main.service.serviceExceptions.RemoveException;

public class FriendsGroupChatController {
    @FXML
    private TableView friendsList;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> avatar;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> fullName;

    @FXML
    private TableColumn<UserFriendshipsDTO, Void> action;

    private UserService userService;
    private FriendshipService friendshipService;
    private User loggedUser;

    public void setServices(UserService userService, FriendshipService friendshipService,User loggedUser){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.loggedUser = loggedUser;
    }

    public void start(){
        friendsList.getColumns().clear();
        friendsList.setItems(FXCollections.observableArrayList(userService.getUserFriendList(loggedUser.getId())));
        addImageToTable();
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        friendsList.getColumns().addAll(fullName);
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<UserFriendshipsDTO, Void>, TableCell<UserFriendshipsDTO, Void>> cellFactory = new Callback<TableColumn<UserFriendshipsDTO, Void>, TableCell<UserFriendshipsDTO, Void>>() {
            @Override
            public TableCell<UserFriendshipsDTO, Void> call(final TableColumn<UserFriendshipsDTO, Void> param) {
                final TableCell<UserFriendshipsDTO, Void> cell = new TableCell<UserFriendshipsDTO, Void>() {

                    private final Button btn = new Button("Add to groupchat");

                    {
                        btn.getStyleClass().add("add-friend-btn");
                        btn.setOnAction((ActionEvent event) -> {
                            UserFriendshipsDTO data = getTableView().getItems().get(getIndex());
                            try {
                                System.out.println("Creating gc");
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
        friendsList.getColumns().add(action);
    }

    private void addImageToTable(){
        avatar.setCellFactory(new Callback<TableColumn<UserFriendshipsDTO, String>, TableCell<UserFriendshipsDTO, String>>() {
            @Override
            public TableCell<UserFriendshipsDTO, String> call(TableColumn<UserFriendshipsDTO, String> param) {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
                TableCell<UserFriendshipsDTO, String> cell = new TableCell<UserFriendshipsDTO, String>() {
                    public void updateItem(String item, boolean empty) {
                        if (item != null) {
                            Image image = new Image(item, false);
                            imageView.setImage(image);
                        }
                    }
                };
                cell.setGraphic(imageView);
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        avatar.setCellValueFactory(new PropertyValueFactory<UserFriendshipsDTO, String>("imageURL"));

        friendsList.getColumns().add(avatar);
    }
}
