package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.domain.FriendRequest;
import main.domain.Login;
import main.domain.User;
import main.service.FriendRequestService;
import main.service.FriendshipService;
import main.service.MessageService;
import main.service.UserService;
import main.service.serviceExceptions.FindException;
import main.utils.AES256;
import sn.socialnetwork.MainApp;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML
    private Button loginButton;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private TextField textUsername;

    @FXML
    private TextField textPassword;

    @FXML
    private BorderPane root;

    private UserService userService;
    private FriendRequestService friendRequestService;
    private FriendshipService friendshipService;
    private MessageService messageService;

    public void setServicesLogin(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService,MessageService messageService){
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        getFocusFromFirstTextfield();
    }

    private void getFocusFromFirstTextfield(){
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);
        textUsername.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                if(newValue && firstTime.get()){
                    root.requestFocus();
                    firstTime.setValue(false);
                }
            }
        });
    }

    public void loginButtonAction(ActionEvent event) {
        if(textUsername.getText().equals("")){
            loginErrorLabel.setText("Username cannot be empty!");
        }
        else {
            String username = textUsername.getText();
            String password = textPassword.getText();
            Login loginData = userService.findRegisteredUser(username);
            if(loginData == null) {
                loginErrorLabel.setText("We couldn't find that username!");
                textUsername.setText(null);
                textPassword.setText(null);
                return;
            }
            AES256 passwordEncrypter = new AES256();
            String decryptedPassword =  passwordEncrypter.decrypt(loginData.getPassword());
            if(!Objects.equals(decryptedPassword, password)) {
                loginErrorLabel.setText("Incorrect password!");
                textUsername.setText(null);
                textPassword.setText(null);
                return;
            }
            else {
                User connectedUser= userService.findUserById(loginData.getUserID());
                connectUser(connectedUser, event);
            }
        }
        textUsername.setText(null);
        textPassword.setText(null);
    }

    public void connectUser(User connectedUser, ActionEvent actionEvent)
    {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/user-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            UserController userController = fxmlLoader.getController();
            userController.loadAppLoggedUser(userService, friendshipService, friendRequestService, messageService, connectedUser);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        //Window window = ((Node) actionEvent.getSource()).getScene().getWindow();
        //stage.initOwner(window);
        stage.sizeToScene();
        stage.setScene(scene);
        stage.setTitle("Blooming");
        stage.setResizable(false);
        stage.getIcons().add(new Image("imgs/app_icon.png"));
        stage.show();
    }

    public void registerButtonPressed(MouseEvent mouseEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/register-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            RegisterController registerController = fxmlLoader.getController();
            registerController.setRegisterController(userService, stage);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Registring");
        stage.setScene(scene);
        stage.getIcons().add(new Image("imgs/app_icon.png"));
        stage.show();
    }

    public void forgotPasswordAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/forgot-password-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            ForgotPasswordController forgotPasswordController = fxmlLoader.getController();
            forgotPasswordController.setRegisterController(userService, stage);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Forgot Password");
        stage.setScene(scene);
        stage.getIcons().add(new Image("imgs/app_icon.png"));
        stage.show();
    }
}
