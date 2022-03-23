package controller;

import controller.pages.PageObject;
import javafx.collections.FXCollections;
import javafx.css.converter.StringConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.domain.UserFriendshipsDTO;
import org.controlsfx.control.spreadsheet.StringConverterWithFormat;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportController {

    @FXML
    private AnchorPane root;

    @FXML
    private ComboBox friendList;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private Label messageLabel;

    @FXML
    private Button firstBtn;

    @FXML
    private Button secondBtn;

    @FXML
    private Label errorDate1;

    @FXML
    private Label errorDate2;

    @FXML
    private Label errorUser;

    private PageObject pageObject;

    public void init(PageObject pageObject){
        this.pageObject = pageObject;
        messageLabel.setVisible(false);
        setComboBox();
        changeDataPickerFormat(fromDate);
        changeDataPickerFormat(toDate);
        friendList.setPromptText("Choose an user");
        fromDate.setPromptText(LocalDate.now().toString());
        toDate.setPromptText(LocalDate.now().toString());
        firstBtn.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                messageLabel.setVisible(false);
                errorDate2.setVisible(false);
                errorDate1.setVisible(false);
                errorUser.setVisible(false);
            }
        });
        secondBtn.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                messageLabel.setVisible(false);
                errorDate2.setVisible(false);
                errorDate1.setVisible(false);
                errorUser.setVisible(false);
            }
        });
    }

    private void setComboBox(){
        List<UserFriendshipsDTO> friends = pageObject.getService().getUserService().getUserFriendList(pageObject.getLoggedUser().getId(), -1, -1);
        List<String> users = friends.stream()
                .map( f -> f.getFriendLastName() + " " + f.getFriendFirstName())
                .collect(Collectors.toList());
                new ArrayList<>();
        friendList.setItems(FXCollections.observableArrayList(users));
    }

    private void changeDataPickerFormat(DatePicker datePicker){
        datePicker.setConverter(new StringConverterWithFormat<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            {
                datePicker.setPromptText(pattern.toLowerCase());
            }

            @Override public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    public void handleFirstPDF(ActionEvent actionEvent) {
        LocalDate localDate1 = fromDate.getValue();
        LocalDate localDate2 = toDate.getValue();
        int index = friendList.getSelectionModel().getSelectedIndex();
        if(!verify(localDate1, localDate2, index)) {
            LocalDateTime localDateTime1 = localDate1.atStartOfDay();
            LocalDateTime localDateTime2 = localDate2.atStartOfDay();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            Stage stage = (Stage) root.getScene().getWindow();
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                String url = file.getAbsolutePath();
                String filename = url + "\\firstReport.pdf";
                pageObject.getService().generateFirstPDF(filename, pageObject.getLoggedUser().getId(), localDateTime1, localDateTime2);
                messageLabel.setVisible(true);
            }
        }

    }

    private boolean verify(LocalDate localDate1, LocalDate localDate2, int index){
        boolean errors = false;
        if(localDate1 == null) {
            errorDate1.setVisible(true);
            errors = true;
        }
        if(localDate2 == null) {
            errorDate2.setVisible(true);
            errors = true;
        }
        if(index < 0){
            errorUser.setVisible(true);
            errors = true;
        }
        return errors;
    }

    public void handleSecondPDF(ActionEvent actionEvent) {
        LocalDate localDate1 = fromDate.getValue();
        LocalDate localDate2 = toDate.getValue();
        int index = friendList.getSelectionModel().getSelectedIndex();
        if(!verify(localDate1, localDate2, index)) {
            LocalDateTime localDateTime1 = localDate1.atStartOfDay();
            LocalDateTime localDateTime2 = localDate2.atStartOfDay();
            UserFriendshipsDTO user = pageObject.getService().getUserService().getUserFriendList(pageObject.getLoggedUser().getId(), -1, -1).get(index);
            DirectoryChooser directoryChooser = new DirectoryChooser();
            Stage stage = (Stage) root.getScene().getWindow();
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                String url = file.getAbsolutePath();
                String filename = url + "\\secondReport.pdf";
                pageObject.getService().generateSecondPDF(filename, pageObject.getLoggedUser().getId(), user.getFriendID(), localDateTime1, localDateTime2);
                messageLabel.setVisible(true);
            }
        }
    }
}
