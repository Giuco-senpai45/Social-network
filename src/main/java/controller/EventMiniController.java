package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.domain.User;
import main.service.RoseEventService;
import main.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMiniController {

    @FXML
    private Label eventNameLabel;

    @FXML
    private Label organiserLabel;

    @FXML
    private Label locationlabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea description;

    @FXML
    private ImageView stopGoingImg;

    @FXML
    private ImageView participatingImage;

    @FXML
    private ImageView eventImage;

    private RoseEventService eventService;
    private UserService userService;

    public void setServices(RoseEventService eventService, UserService userService){
        this.eventService = eventService;
        this.userService = userService;
    }

    public void setContent(String imageUrl, User organiser, String location, String eventName, LocalDateTime date){
        Image image = new Image(imageUrl);
        eventImage.setPreserveRatio(true);
        eventImage.setImage(image);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // 'at' hh:mm");
        dateLabel.setText(date.format(formatter));
        eventNameLabel.setText(eventName);
        organiserLabel.setText("Organiser: " +  organiser.getFirstName() + " " +  organiser.getLastName());
        locationlabel.setText("Location: " + location);
    }
}
