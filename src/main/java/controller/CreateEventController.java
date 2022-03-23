package controller;

import controller.pages.PageObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.domain.Chat;
import main.domain.User;
import main.service.RoseEventService;
import main.service.serviceExceptions.AddException;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CreateEventController {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField locationField;

    @FXML
    private ComboBox day;

    @FXML
    private ComboBox month;

    @FXML
    private ComboBox year;

    @FXML
    private ComboBox hour;

    @FXML
    private ComboBox minute;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button createEventBtn;

    @FXML
    private Button addImageBtn;

    @FXML
    private TextField nameField;

    @FXML
    private Label locationError;

    @FXML
    private Label successLabel;

    @FXML
    private Label dateError;

    @FXML
    private Label nameError;

    private String d;
    private String m;
    private String y;
    private String h;
    private String mn;
    private String img_url;
    private PageObject pageObject;
    private User loggedUser;
    private RoseEventService eventService;

    public void init(PageObject pageObject, User loggedUser) {
        this.pageObject = pageObject;
        this.eventService = pageObject.getService().getEventService();
        this.loggedUser = loggedUser;
        img_url = null;
        getFocusFromFirstTextField();
        setComboBoxes();
    }

    private void getFocusFromFirstTextField(){
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);
        locationField.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                if(newValue && firstTime.get()){
                    root.requestFocus();
                    firstTime.setValue(false);
                }
            }
        });
    }

    public void setComboBoxes(){
        List<Integer> dayList = new ArrayList<>();
        List<Integer> monthList = new ArrayList<>();
        List<Integer> yearList = new ArrayList<>();
        List<Integer> hourList = new ArrayList<>();
        List<Integer> minuteList = new ArrayList<>();
        for(int i = 1; i <= 31; i++)
            dayList.add(i);
        for(int i = 1; i <= 12; i++)
            monthList.add(i);
        for(int i = LocalDate.now().getYear(); i <=LocalDate.now().getYear() + 20 ; i++)
            yearList.add(i);
        for(int i = 0; i <= 23; i++)
            hourList.add(i);
        for(int i = 0; i <= 59; i++)
            minuteList.add(i);
        day.setItems(FXCollections.observableArrayList(dayList));
        month.setItems(FXCollections.observableArrayList(monthList));
        year.setItems(FXCollections.observableArrayList(yearList));
        hour.setItems(FXCollections.observableArrayList(hourList));
        minute.setItems(FXCollections.observableArrayList(minuteList));
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

        if((int) hour.getValue()>9)
            h = hour.getValue().toString();
        else
            h = "0" + hour.getValue().toString();

        if((int) minute.getValue()>9)
            mn = minute.getValue().toString();
        else
            mn = "0" + minute.getValue().toString();

        y = year.getValue().toString();

    }

    public void handleCreateEvent(ActionEvent actionEvent) {
        String location = locationField.getText();
        String name = nameField.getText();
        String description = descriptionArea.getText();
        String eventImage = img_url;
        LocalDate date = null;
        LocalDateTime dateTime = null;
        try {
            getDate();
            try {
                date = LocalDate.parse(y + "-" + m + "-" + d);
                dateTime = date.atTime(Integer.parseInt(h),Integer.parseInt(mn));
            } catch (DateTimeParseException e) {
                dateError.setText("Please select a valid date!");
            }
        }
        catch (NullPointerException e) {
            dateError.setText("Please select a valid date!");
        }
        try {
            eventService.addEvent(location, name, dateTime, description, eventImage, loggedUser.getId());
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        } catch (AddException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleAddImageEvent(ActionEvent actionEvent) {
        if(successLabel.isVisible()){
            successLabel.setVisible(false);
        }
        FileChooser fileChooser = new FileChooser();
        Stage stage =(Stage) root.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            img_url = file.getAbsolutePath();
            successLabel.setVisible(true);
        }
    }
}
