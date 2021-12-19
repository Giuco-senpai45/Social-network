package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import main.domain.Friendship;
import main.domain.Tuple;
import main.domain.User;
import main.domain.UserFriendshipsDTO;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.UserService;


public class FriendsController{

    @FXML
    private TableView friendsList;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> fullName;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> friendshipDate;

    @FXML
    private TableColumn<UserFriendshipsDTO, Void> action;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private User loggedUser;

    public void setController(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, User loggedUser){
        this.userService = userService;
        System.out.println(userService.getUsers());
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.loggedUser = loggedUser;
    }

    public void start(){
        friendsList.getColumns().clear();

        friendsList.setItems(FXCollections.observableArrayList(userService.getUserFriendList(loggedUser.getId())));
        fullName.setCellValueFactory(new PropertyValueFactory<>("friendFirstName" + " " + "friendLastName"));
        friendshipDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        friendsList.getColumns().addAll(fullName, friendshipDate);

        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<UserFriendshipsDTO, Void>, TableCell<UserFriendshipsDTO, Void>> cellFactory = new Callback<TableColumn<UserFriendshipsDTO, Void>, TableCell<UserFriendshipsDTO, Void>>() {
            @Override
            public TableCell<UserFriendshipsDTO, Void> call(final TableColumn<UserFriendshipsDTO, Void> param) {
                final TableCell<UserFriendshipsDTO, Void> cell = new TableCell<UserFriendshipsDTO, Void>() {

                    private final Button btn = new Button("Remove friend");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            UserFriendshipsDTO data = getTableView().getItems().get(getIndex());
                            friendshipService.removeFriendship(new Tuple<>(loggedUser.getId(), data.getFriendID()));
                            friendsList.getItems().remove(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        action.setCellFactory(cellFactory);
        friendsList.getColumns().add(action);
    }
}
