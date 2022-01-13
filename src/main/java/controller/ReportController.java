package controller;

import controller.pages.PageObject;
import javafx.collections.FXCollections;
import javafx.css.converter.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import main.domain.UserFriendshipsDTO;
import org.controlsfx.control.spreadsheet.StringConverterWithFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportController {


    @FXML
    private ComboBox friendList;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private Label messageLabel;

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
        LocalDateTime localDateTime1 = localDate1.atStartOfDay();
        LocalDate localDate2 = toDate.getValue();
        LocalDateTime localDateTime2 = localDate2.atStartOfDay();
        pageObject.getService().generateFirstPDF(pageObject.getLoggedUser().getId(), localDateTime1, localDateTime2);
        messageLabel.setVisible(true);
    }

    public void handleSecondPDF(ActionEvent actionEvent) {
        LocalDate localDate1 = fromDate.getValue();
        LocalDateTime localDateTime1 = localDate1.atStartOfDay();
        LocalDate localDate2 = toDate.getValue();
        LocalDateTime localDateTime2 = localDate2.atStartOfDay();
        int index = friendList.getSelectionModel().getSelectedIndex();
        UserFriendshipsDTO user = pageObject.getService().getUserService().getUserFriendList(pageObject.getLoggedUser().getId(), -1, -1).get(index);
        pageObject.getService().generateSecondPDF(pageObject.getLoggedUser().getId(), user.getFriendID(), localDateTime1, localDateTime2);
        messageLabel.setVisible(true);
    }
}
