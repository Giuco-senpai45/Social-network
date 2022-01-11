package controller;

import controller.pages.PageObject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import main.domain.FriendRequest;
import main.domain.FriendRequestUserDTO;
import main.domain.User;
import main.utils.Observer;
import main.utils.events.FriendRequestEvent;

import java.util.List;
import java.util.stream.Collectors;

public class FriendRequestsControll implements Observer<FriendRequestEvent> {

    @FXML
    private ListView<FriendRequestUserDTO> friendRequestListView;

    @FXML
    private Label friendRequestsLabel;

    @FXML
    private Button historyButton;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    private PageObject pageObject;

    public void initialise(PageObject pageObject){
        this.pageObject = pageObject;
        this.pageObject.getService().getFriendRequestService().addObserver(this);
    }

    private List<FriendRequestUserDTO> insertFriendRequest(List<FriendRequest> friendRequestList){
        return friendRequestList.stream()
                .map((req) ->{
                    User user = pageObject.getService().getUserService().findUserById(req.getFrom());
                   return new FriendRequestUserDTO(user.getFirstName()+ " " + user.getLastName(),
                           user.getId(),req.getId(),req.getStatus(),req.getTime());
                }
                )
                .collect(Collectors.toList());
    }

    public void showCurrentFriendRequests(){
        List<FriendRequest> friendRequests = pageObject.getService().getFriendRequestService().findPendingRequestsForUser(pageObject.getLoggedUser().getId());
        System.out.println(friendRequests);
        if(friendRequests.size() == 0){
            friendRequestListView.setVisible(false);
            friendRequestsLabel.setText("You currently have no pending friend requests.");
        }
        else {
            friendRequestsLabel.setText("Current friend requests");
            System.out.println("ok");
            System.out.println(insertFriendRequest(friendRequests));
            friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(friendRequests)));
            friendRequestListView.setVisible(true);
        }
    }

    public void updateFriendRequests(){
        friendRequestListView.getItems().clear();
        List<FriendRequest> friendRequests = pageObject.getService().getFriendRequestService().findPendingRequestsForUser(pageObject.getLoggedUser().getId());
        friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(friendRequests)));
    }

    public void acceptRequestEvent(MouseEvent mouseEvent) {
        FriendRequestUserDTO friendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        pageObject.getService().getFriendRequestService().processRequest(friendRequest.getFriendRequestID(),"approved");
        pageObject.getService().getFriendshipService().addFriendship(pageObject.getLoggedUser().getId(), friendRequest.getUserId());
        updateFriendRequests();
    }

    public void rejectRequestEvent(MouseEvent mouseEvent) {
        FriendRequestUserDTO friendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        pageObject.getService().getFriendRequestService().processRequest(friendRequest.getFriendRequestID(),"rejected");
        updateFriendRequests();
    }

    public void showHistoryEvent(MouseEvent mouseEvent) {
        if(historyButton.getText().equals("History")){
            acceptButton.setVisible(false);
            rejectButton.setVisible(false);
            historyButton.setText("Back");
            friendRequestListView.getItems().clear();
            List<FriendRequest> historyFriendRequests = pageObject.getService().getFriendRequestService().getHistoryRequests(pageObject.getLoggedUser().getId());
            friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(historyFriendRequests)));
        }
        else {
            acceptButton.setVisible(true);
            rejectButton.setVisible(true);
            historyButton.setText("History");
            friendRequestListView.getItems().clear();
            List<FriendRequest> friendRequests = pageObject.getService().getFriendRequestService().findPendingRequestsForUser(pageObject.getLoggedUser().getId());
            friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(friendRequests)));
        }
    }

    @Override
    public void update(FriendRequestEvent friendRequestEvent) {
        showCurrentFriendRequests();
    }
}
