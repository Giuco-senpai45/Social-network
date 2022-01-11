package controller;

import controller.pages.PageObject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.domain.Chat;
import main.domain.User;
import main.domain.UserFriendshipsDTO;
import main.service.FriendshipService;
import main.service.MasterService;
import main.service.MessageService;
import main.service.UserService;
import main.service.serviceExceptions.RemoveException;

import java.util.ArrayList;
import java.util.List;

public class FriendsGroupChatController {
    @FXML
    private TableView friendsList;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> avatar;

    @FXML
    private TableColumn<UserFriendshipsDTO, String> fullName;

    @FXML
    private TableColumn<UserFriendshipsDTO, Void> action;

    private PageObject pageObject;
    private Chat createdChat;
    private List<Long> usersInNewGroup;

    public void setServices(PageObject pageObject){
        this.pageObject = pageObject;
        usersInNewGroup = new ArrayList<>();
        this.createdChat = null;
    }

    public void start(){
        friendsList.getColumns().clear();
        friendsList.setItems(FXCollections.observableArrayList(pageObject.getService().getUserService().getUserFriendList(pageObject.getLoggedUser().getId())));
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
                    private CheckBox checkBox = new CheckBox("Add");

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        }
                        else {
                            checkBox.setOnAction((ActionEvent event) -> {
                                UserFriendshipsDTO data = getTableView().getItems().get(getIndex());
                                if(checkBox.isSelected()){
                                    usersInNewGroup.add(data.getFriendID());
                                }
                                else {
                                    usersInNewGroup.remove(data.getFriendID());
                                }
                            });
                            checkBox.setAlignment(Pos.CENTER);
                            setGraphic(checkBox);
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
                Circle circle = new Circle(40);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                TableCell<UserFriendshipsDTO, String> cell =  new TableCell<UserFriendshipsDTO, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            Image image = new Image(item, false);
                            imageView.setImage(image);
                            circle.setFill(new ImagePattern(image));
                            setCursor(Cursor.HAND);
                            setGraphic(circle);
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
        avatar.setCellValueFactory(new PropertyValueFactory<UserFriendshipsDTO, String>("imageURL"));
        friendsList.getColumns().add(avatar);
    }

    public Chat getCreatedChat() {
        return createdChat;
    }

    public void handleCreateGroupAction(ActionEvent actionEvent) {
        usersInNewGroup.add(pageObject.getLoggedUser().getId());
        createdChat = pageObject.getService().getMessageService().createNewChatGroup(usersInNewGroup);
        Stage stage =(Stage) friendsList.getScene().getWindow();
        stage.close();
    }
}
