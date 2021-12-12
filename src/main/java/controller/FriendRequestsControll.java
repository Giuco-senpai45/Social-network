package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import main.domain.FriendRequest;
import main.domain.FriendRequestUserDTO;
import main.domain.User;
import main.service.FriendRequestService;
import main.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class FriendRequestsControll {
    private UserService userService;
    private FriendRequestService friendRequestService;
    private User loggedUser;

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

    public void initialise(UserService userService, FriendRequestService friendRequestService,User user){
        this.loggedUser = user;
        this.userService = userService;
        this.friendRequestService = friendRequestService;
    }

    private List<FriendRequestUserDTO> insertFriendRequest(List<FriendRequest> friendRequestList){
        return friendRequestList.stream()
                .map((req) ->{
                    User user = userService.findUserById(req.getFrom());
                   return new FriendRequestUserDTO(user.getFirstName()+ " " + user.getLastName(),
                           user.getId(),req.getId(),req.getStatus(),req.getTime());
                }
                )
                .collect(Collectors.toList());
    }

    public void showCurrentFriendRequests(){
        List<FriendRequest> friendRequests = friendRequestService.findPendingRequestsForUser(loggedUser.getId());
        if(friendRequests.size() == 0){
            friendRequestListView.setVisible(false);
            friendRequestsLabel.setText("You currently have no pending friend requests.");
        }
        else {
            friendRequestsLabel.setText("Current friend requests");
            friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(friendRequests)));
        }
    }

    public void updateFriendRequests(){
        friendRequestListView.getItems().clear();
        List<FriendRequest> friendRequests = friendRequestService.findPendingRequestsForUser(loggedUser.getId());
        friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(friendRequests)));
    }

    public void acceptRequestEvent(MouseEvent mouseEvent) {
        FriendRequestUserDTO friendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        friendRequestService.processRequest(friendRequest.getFriendRequestID(),"approved");
        updateFriendRequests();
    }

    public void rejectRequestEvent(MouseEvent mouseEvent) {
        FriendRequestUserDTO friendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        friendRequestService.processRequest(friendRequest.getFriendRequestID(),"rejected");
        updateFriendRequests();
    }

    public void showHistoryEvent(MouseEvent mouseEvent) {
        if(historyButton.getText().equals("History")){
            acceptButton.setVisible(false);
            rejectButton.setVisible(false);
            historyButton.setText("Back");
            friendRequestListView.getItems().clear();
            List<FriendRequest> historyFriendRequests = friendRequestService.getHistoryRequests(loggedUser.getId());
            historyFriendRequests.forEach(System.out::println);
            friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(historyFriendRequests)));
        }
        else {
            acceptButton.setVisible(true);
            rejectButton.setVisible(true);
            historyButton.setText("History");
            friendRequestListView.getItems().clear();
            List<FriendRequest> friendRequests = friendRequestService.findPendingRequestsForUser(loggedUser.getId());
            friendRequestListView.setItems(FXCollections.observableArrayList(insertFriendRequest(friendRequests)));
        }
    }
}
