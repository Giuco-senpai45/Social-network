package controller;

import controller.pages.PageObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import main.domain.*;
import main.service.MasterService;
import main.service.serviceExceptions.RemoveException;
import main.utils.Observer;
import main.utils.events.FriendDeletionEvent;


public class FriendsController implements Observer<FriendDeletionEvent> {

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

    private PageObject pageObject;
    private ObservableList<UserFriendshipsDTO> model = FXCollections.observableArrayList();

    public void setController(PageObject pageObject){
        this.pageObject = pageObject;
        this.pageObject.getService().getFriendshipService().addObserver(this);
    }

    public void start(){
        model.setAll(pageObject.getService().getUserService().getUserFriendList(pageObject.getLoggedUser().getId()));
        addImageToTable();
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        friendshipDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        addButtonToTable();
        friendsList.setItems(model);
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
                                FriendRequest friendRequest = pageObject.getService().getFriendRequestService().findFriendRequest(pageObject.getLoggedUser().getId(), data.getFriendID());
                                //masterService.getFriendRequestService().processRequest(friendRequest.getId(), "deleted");
                                //friendsList.getItems().remove(data);
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
        //friendsList.getColumns().add(avatar);
    }

    @Override
    public void update(FriendDeletionEvent friendDeletionEvent) {
        start();
    }
}
