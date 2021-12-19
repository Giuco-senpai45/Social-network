package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.domain.Login;
import main.domain.User;
import main.domain.validators.UserValidator;
import main.service.UserService;
import sn.socialnetwork.MainApp;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterController {

    @FXML
    private ComboBox day;

    @FXML
    private ComboBox month;

    @FXML
    private ComboBox year;

    @FXML
    private RadioButton male;

    @FXML
    private RadioButton female;

    @FXML
    private RadioButton other;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField address;

    @FXML
    private TextField email;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField retypedPasswd;

    @FXML
    private Label firstNameError;

    @FXML
    private Label lastNameError;

    @FXML
    private Label addressError;

    @FXML
    private Label dateError;

    @FXML
    private Label genderError;

    @FXML
    private Label emailError;

    @FXML
    private Label usernameError;

    @FXML
    private Label passwordError;

    @FXML
    private Label retypedPasswordError;

    @FXML
    private Label generalError;


    private UserService userService;
    private UserValidator userValidator;
    private String d;
    private String m;
    private String y;
    private Stage stage;
    private String gender = null;

    public void setRegisterController(UserService userService, Stage stage){
        this.userService = userService;
        this.userValidator = new UserValidator();
        this.stage = stage;
        initializeErrorLabels();
        setComboBoxes();
    }


    public void setComboBoxes(){
        List<Integer> dayList = new ArrayList<>();
        List<Integer> monthList = new ArrayList<>();
        List<Integer> yearList = new ArrayList<>();
        for(int i = 1; i <= 31; i++)
            dayList.add(i);
        for(int i = 1; i <= 12; i++)
            monthList.add(i);
        for(int i = 1900; i <= 2021; i++)
            yearList.add(i);
        day.setItems(FXCollections.observableArrayList(dayList));
        month.setItems(FXCollections.observableArrayList(monthList));
        year.setItems(FXCollections.observableArrayList(yearList));
    }

    @FXML
    public void handleUserRegister(ActionEvent actionEvent) {
        initializeErrorLabels();
        generalError.setVisible(false);
        String firstN= firstName.getText();
        String lastN = lastName.getText();
        String addr = address.getText();
        LocalDate date = null;
        try {
            getDate();

            try {
                date = LocalDate.parse(y + "-" + m + "-" + d);
            } catch (DateTimeParseException e) {
                dateError.setText("Please select a valid date!");
            }
        }
        catch (NullPointerException e){
            dateError.setText("Please select a valid date!");
        }
        String e = email.getText();
        getGender();
        String usrn = username.getText();
        String passwd = password.getText();
        String reTyped = retypedPasswd.getText();
        if(validateData()) {
            boolean found = false;
            for(Login login: userService.allRegisteredUsers())
                if(Objects.equals(login.getId(), username.getText()) && Objects.equals(login.getPassword(), password.getText()))
                    found = true;
            if(found) {
                generalError.setText("Error! There is already an account with these credentials.");
                generalError.setVisible(true);
            }
            else {
                resetTextFields();
                generalError.setVisible(false);
                showExtraInfoWindow(firstN, lastN, addr, date, e, usrn, passwd);
                stage.close();
            }
        }
        else{
            generalError.setText("Error! Try to introduce your info again.");
            generalError.setVisible(true);
        }
    }

    public boolean validateData(){
        if (!userValidator.validateName(firstName.getText()))
            firstNameError.setText("Invalid first name!");
        if (!userValidator.validateName(lastName.getText()))
            lastNameError.setText("Invalid last name!");
        if (!userValidator.validateName(address.getText()))
            addressError.setText("Invalid address!");
        if (gender == null)
            genderError.setText("Please select one of the options above!");
        if (!userValidator.validateEmail(email.getText()))
            emailError.setText("Invalid email address!");
        for(Login login: userService.allRegisteredUsers())
            if(Objects.equals(login.getId(), username.getText()))
                usernameError.setText("Unavailable username!");
        if(!userValidator.validatePassword(password.getText()))
            passwordError.setText("Your password must have at least 8 characters!");
        else if(!Objects.equals(retypedPasswd.getText(), password.getText()))
            retypedPasswordError.setText("Retyped password doesn't match the first one!");
        return Objects.equals(firstNameError.getText(), "") && Objects.equals(lastNameError.getText(), "") && Objects.equals(addressError.getText(), "") && Objects.equals(dateError.getText(), "") && Objects.equals(genderError.getText(), "") && Objects.equals(emailError.getText(), "") && Objects.equals(usernameError.getText(), "") && Objects.equals(passwordError.getText(), "") && Objects.equals(retypedPasswordError.getText(), "");
    }

    private void getDate() {
        if((int) day.getValue() > 9)
            d = day.getValue().toString();
        else
            d = "0" + day.getValue().toString();

        if((int) month.getValue()>9)
            m = month.getValue().toString();
        else
            m = "0" + month.getValue().toString();

        y = year.getValue().toString();
    }

    private void getGender(){
        if(male.isSelected())
            gender = "male";
        else if(female.isSelected())
            gender = "female";
        else if(other.isSelected())
            gender = "other";
    }

    private void initializeErrorLabels(){
        emailError.setText("");
        firstNameError.setText("");
        lastNameError.setText("");
        addressError.setText("");
        genderError.setText("");
        dateError.setText("");
        usernameError.setText("");
        passwordError.setText("");
        retypedPasswordError.setText("");
    }

    private void resetTextFields(){
        firstName.setText(null);
        lastName.setText(null);
        address.setText(null);
        username.setText(null);
        password.setText(null);
        retypedPasswd.setText(null);
        email.setText(null);
        male.setSelected(false);
        female.setSelected(false);
        other.setSelected(false);
        day.valueProperty().set(null);
        month.valueProperty().set(null);
        year.valueProperty().set(null);
    }

    public void showExtraInfoWindow(String firstN, String lastN, String addr, LocalDate date, String e, String usrn, String passwd){
        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/extrainfo-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            ExtraInfoController extraInfoController = fxmlLoader.getController();
            extraInfoController.setController(userService, newStage, firstN, lastN, addr, gender, date, e, usrn, passwd);
        }
        catch(IOException exception) {
            exception.printStackTrace();
        }
        newStage.setTitle("Welcome to Truth Rose!");
        newStage.setScene(scene);
        newStage.show();
    }

}
