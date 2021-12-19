package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.domain.User;
import main.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ExtraInfoController {

    @FXML
    private AnchorPane root;

    @FXML
    private Label relationshipError;

    @FXML
    private Label schoolError;

    @FXML
    private Label funFactError;

    @FXML
    private TextField school;

    @FXML
    private TextField funFact;

    @FXML
    private ComboBox relationshipStatus;

    private UserService userService;
    private Stage stage;
    private String s = null;
    private String f = null;
    private String r = null;
    private boolean finished = false;
    private String firstN;
    private String lastN;
    private String addr;
    private String gender;
    private LocalDate date;
    private String e;
    private String usrn;
    private String passwd;
    private String imageURL;

    public void setController(UserService userService, Stage stage, String firstN, String lastN, String addr, String gender, LocalDate date, String e, String usrn, String passwd){
        funFactError.setText("");
        schoolError.setText("");
        relationshipError.setText("");
        this.userService = userService;
        this.stage = stage;
        this.firstN = firstN;
        this.lastN = lastN;
        this.addr = addr;
        this.date = date;
        this.e = e;
        this.gender = gender;
        this.usrn = usrn;
        this.passwd = passwd;
        setComboBox();
        getFocusFromFirstTextField();
    }

    private void getFocusFromFirstTextField(){
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);
        school.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                if(newValue && firstTime.get()){
                    root.requestFocus();
                    firstTime.setValue(false);
                }
            }
        });
    }

    private void setComboBox(){
        List<String> status = new ArrayList<>();
        status.add("Single");
        status.add("In a relationship");
        status.add("Engaged");
        status.add("Married");
        status.add("Something else");
        status.add("I prefer not to say");
        relationshipStatus.setItems(FXCollections.observableArrayList(status));
    }

    @FXML
    public void handleFinishRegister(ActionEvent actionEvent) {
        funFactError.setText("");
        schoolError.setText("");
        relationshipError.setText("");
        s = school.getText();
        f = funFact.getText();
        try {
            r = relationshipStatus.getValue().toString();
        }
        catch(NullPointerException e){
            relationshipError.setText("Please choose an option!");
            relationshipError.setVisible(true);
        }
        if(validateFields()){
            generateAvatar();
            userService.addUser(firstN, lastN, addr, date, gender, e, s, r, f, imageURL);
            userService.loginUser(usrn, passwd, userService.getCurrentUserID());
            stage.close();
        }
    }

    private boolean validateFields(){
        if(s == null) {
            schoolError.setText("Invalid school!");
            schoolError.setVisible(true);
        }
        if(f == null){
            funFactError.setText("Invalid fun fact!");
            funFactError.setVisible(true);
        }
        if (Objects.equals(relationshipError.getText(), "") && Objects.equals(schoolError.getText(), "") && Objects.equals(funFactError.getText(), ""))
            return true;
        return false;
    }

    private void generateAvatar(){
        List<String> avatars = new ArrayList<>();
        String url = "/imgs/avatars/image_part_0";
        for(int i = 1; i < 10; i++)
            avatars.add(url + "0" + i + ".jpg");
        for(int i = 10; i <= 65; i++)
            avatars.add(url + i + ".jpg");

        Random r = new Random();
        imageURL = avatars.get(r.nextInt(avatars.size()));
    }
}
