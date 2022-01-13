package controller;

import controller.pages.PageObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.domain.Login;
import main.domain.User;
import main.service.*;
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

    private PageObject pageObject;
    private MasterService masterService;

    public void setServiceLogin(MasterService masterService){
        this.masterService = masterService;
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
            Login loginData = masterService.getUserService().findRegisteredUser(username);
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
                User connectedUser= masterService.getUserService().findUserById(loginData.getUserID());
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

            pageObject = new PageObject(masterService, connectedUser);

            UserController userController = fxmlLoader.getController();
            userController.loadAppLoggedUser(pageObject, stage);
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
            registerController.setRegisterController(masterService, stage);
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
            forgotPasswordController.setRegisterController(pageObject, stage);
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
