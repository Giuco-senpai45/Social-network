package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.domain.User;
import main.service.UserService;


public class UserProfileController {

    @FXML
    private Label profileNameLabel;

    private User currentUser;
    private UserService userService;

    public void createUserProfile(UserService userService,User user){
        this.userService = userService;
        profileNameLabel.setText(null);
        profileNameLabel.setText(user.getLastName() + "  "  + user.getFirstName());
    }
}
