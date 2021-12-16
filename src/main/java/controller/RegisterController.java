package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import main.domain.User;
import main.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private UserService userService;
    private String d;
    private String m;
    private String y;
    private String gender;

    public void setRegisterController(UserService userService){
        this.userService = userService;
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

        String firstN= firstName.getText();
        String lastN = lastName.getText();
        String addr = address.getText();
        LocalDate date = LocalDate.parse(y + "-" + m + "-" + d);
        String e = email.getText();
        if(male.isSelected())
            gender = "male";
        else if(female.isSelected())
            gender = "female";
        else if(other.isSelected())
            gender = "other";
        String usrn = username.getText();
        String passwd = password.getText();
        String reTyped = retypedPasswd.getText();
        userService.addUser(firstN, lastN,  addr, date, gender, e);
        userService.loginUser(usrn, passwd, userService.getCurrentUserID());

    }

    public void handleDaySelection(ActionEvent actionEvent) {
        if((int) day.getValue() > 9)
            d = day.getValue().toString();
        else
            d = "0" + day.getValue().toString();
    }

    public void handleMonthSelection(ActionEvent actionEvent) {

        if((int) month.getValue()>9)
            m = month.getValue().toString();
        else
            m = "0" + month.getValue().toString();
    }

    public void handleYearSelection(ActionEvent actionEvent) {

        y = year.getValue().toString();
    }

    public void handleMaleSelection(ActionEvent actionEvent) {
    }

    public void handleFemaleSelection(ActionEvent actionEvent) {
    }

    public void handleOtherSelection(ActionEvent actionEvent) {
    }
}
