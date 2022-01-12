package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.domain.RoseEvent;
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
    private TextArea txtDescription;

    @FXML
    private ImageView stopGoingImg;

    @FXML
    private ImageView participatingImage;

    @FXML
    private Button participateButton;

    @FXML
    private ImageView eventImage;

    private RoseEventService eventService;
    private UserService userService;
    private User loggedUser;
    private RoseEvent currentEvent;

    public void setServices(RoseEventService eventService, UserService userService,User loggedUser){
        this.eventService = eventService;
        this.userService = userService;
        this.loggedUser = loggedUser;
    }

    public void setContent(RoseEvent event){
        currentEvent = event;
        String imageUrl = event.getEventUrl();
        User organiser = userService.findUserById(event.getOrganiser());
        LocalDateTime date =event.getDate();
        String location = "\uD83D\uDCCD Location: " + event.getLocation();
        String eventName = event.getEventName();
        String description = event.getDescription();

        if(event.getParticipants().contains(loggedUser.getId())){
            //Participating in the event
            participatingImage.setVisible(false);
            stopGoingImg.setVisible(true);
            participateButton.setText("Stop going");
        }
        else{
            participatingImage.setVisible(true);
            stopGoingImg.setVisible(false);
            participateButton.setText("Interested");
        }

        Image image = new Image(imageUrl);
        eventImage.setPreserveRatio(true);
        eventImage.setImage(image);
        centerImage();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // 'at' hh:mm");
        dateLabel.setText("\uD83D\uDCC5 Date: " + date.format(formatter));
        eventNameLabel.setText(eventName);
        organiserLabel.setText("\uD83D\uDC64 Organiser: " +  organiser.getFirstName() + " " +  organiser.getLastName());
        locationlabel.setText(location);
        txtDescription.setText(description);
        txtDescription.setWrapText(true);
        txtDescription.setEditable(false);
    }

    public void centerImage() {
        Image img = eventImage.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = eventImage.getFitWidth() / img.getWidth();
            double ratioY = eventImage.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            eventImage.setX((eventImage.getFitWidth() - w) / 2);
            eventImage.setY((eventImage.getFitHeight() - h) / 2);
        }
    }

    public void btnParticipateAction(ActionEvent actionEvent) {
        if(participateButton.getText().equals("Interested")){
            participatingImage.setVisible(false);
            stopGoingImg.setVisible(true);
            participateButton.setText("Stop going");
            eventService.addUserToEvent(loggedUser.getId(),currentEvent);
        }
        else if(participateButton.getText().equals("Stop going")){
            participatingImage.setVisible(true);
            stopGoingImg.setVisible(false);
            participateButton.setText("Interested");
            eventService.removeUserFromEvent(loggedUser.getId(),currentEvent);
        }
    }
}
