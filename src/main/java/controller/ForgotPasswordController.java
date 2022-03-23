package controller;

import controller.pages.PageObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import main.domain.Login;
import main.service.UserService;
import main.service.serviceExceptions.FindException;
import main.service.serviceExceptions.UpdateException;
import main.utils.AES256;

public class ForgotPasswordController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField retypedPasswordField;

    @FXML
    private Label passwordError;

    private PageObject pageObject;
    private Stage stage;
    private Login newLogin;

    public void setRegisterController(PageObject pageObject, Stage stage) {
        this.pageObject = pageObject;
        this.stage = stage;
        newLogin = null;
    }

    public void usernameAction(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            passwordError.setVisible(false);
            try {
                newLogin = pageObject.getService().getUserService().findUserByUsername(usernameField.getText());
                if(newLogin != null){
                    passwordField.setVisible(true);
                    retypedPasswordField.setVisible(true);
                }
            }
            catch (FindException e) {
                passwordError.setText(e.getMessage());
                passwordError.setVisible(true);
            }
        }
    }

    public void retypedPasswordKey(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            passwordError.setText("");
            passwordError.setVisible(true);
            if (!passwordField.getText().equals("")) {
                if (passwordField.getText().equals(retypedPasswordField.getText())) {
                    AES256 encrypter = new AES256();
                    if (encrypter.decrypt(newLogin.getPassword()).equals(passwordField.getText())) {
                        passwordError.setText("New password cannot be the same");
                        passwordError.setVisible(true);
                        return;
                    }
                    String encryptedPassword = encrypter.encrypt(passwordField.getText());
                    newLogin.setPassword(encryptedPassword);
                    try {
                        pageObject.getService().getUserService().updateLoginInformation(newLogin);
                        stage.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Password Changed");
                        alert.setHeaderText("The password for " + usernameField.getText() + " was change successfully");
                        alert.show();
                    } catch (UpdateException e) {
                        passwordError.setText(e.getMessage());
                        passwordError.setVisible(true);
                        passwordField.clear();
                        retypedPasswordField.clear();
                    }
                }
            }
        }
    }
}
