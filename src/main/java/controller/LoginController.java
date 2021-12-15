package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.domain.FriendRequest;
import main.domain.User;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.UserService;
import main.service.serviceExceptions.FindException;
import sn.socialnetwork.MainApp;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button loginButton;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private TextField textUsername;

    @FXML
    private TextField textPassword;

    private UserService userService;
    private FriendRequestService friendRequestService;
    private FriendshipService friendshipService;

    public void setServicesLogin(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService){
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.friendshipService = friendshipService;
    }

    public void loginButtonAction(ActionEvent event) {
        if(textUsername.getText().isBlank()){
            loginErrorLabel.setText("Username cannot be empty!");
        }
//        else if(textPassword.getText().isBlank()){
//            loginErrorLabel.setText("Password cannot be empty!");
//        }
        else {
            Long userId = Long.parseLong(textUsername.getText());
            try{
                User connectedUser= userService.findUserById(userId);
                connectUser(connectedUser);
            }
            catch (FindException e){
                System.out.println(e.getMessage());
            }
        }
        textUsername.setText(null);
        textPassword.setText(null);
    }

    public void connectUser(User connectedUser)
    {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/user-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            UserController userController = fxmlLoader.getController();
            userController.loadAppLoggedUser(userService, friendshipService, friendRequestService, connectedUser);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Blooming");
        stage.setWidth(900);
        stage.setHeight(800);
        stage.setScene(scene);
        stage.show();
    }

    public void registerButtonPressed(MouseEvent mouseEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/register-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            RegisterController registerController = fxmlLoader.getController();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Registring");
        stage.setWidth(700);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.show();
    }
}
