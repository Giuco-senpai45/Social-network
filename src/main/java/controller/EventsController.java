package controller;

import controller.pages.PageObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Callback;
import main.domain.RoseEvent;
import main.domain.User;
import main.service.RoseEventService;
import main.service.UserService;
import sn.socialnetwork.MainApp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

public class EventsController {

    @FXML
    private BorderPane parentBorderPane;
    private RoseEventService eventService;
    private UserService userService;
    private User loggedUser;
    private PageObject pageObject;

    public void init(PageObject pageObject)
    {
        this.pageObject = pageObject;
        setServicesEvents();
    }

    public void setServicesEvents() {
        this.eventService = pageObject.getService().getEventService();
        this.userService = pageObject.getService().getUserService();
        this.loggedUser = pageObject.getLoggedUser();
    }

    public GridPane createPage(Integer pageIndex)
    {
        Set<RoseEvent> events = eventService.getEventsOnPage(pageIndex, loggedUser.getId());
        for(RoseEvent event : events){
            System.out.println(event);
        }
        GridPane eventsPane = new GridPane();
        eventsPane.setPrefHeight(540);
        eventsPane.setPrefWidth(600);

        int columnCount = 1;
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPrefWidth(630);
        cc.setHgrow(Priority.ALWAYS);
        cc.setHalignment(HPos.CENTER);
        for (int i = 0; i < columnCount; i++) {
            eventsPane.getColumnConstraints().add(cc);
        }

        int rowCount = 3;
        RowConstraints rc = new RowConstraints();
        rc.setPrefHeight(300);
        rc.setVgrow(Priority.ALWAYS);
        rc.setValignment(VPos.CENTER);
        for (int i = 0; i < rowCount; i++) {
            eventsPane.getRowConstraints().add(rc);
        }

        Pane firstPane = new Pane();
        EventMiniController eventMiniController = null;
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/views/event-mini-view.fxml"));
            firstPane = loader.load();
            eventMiniController = loader.getController();
            eventMiniController.setServices(eventService,userService);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        Pane secondPane = new Pane();
        EventMiniController eventMiniController2 = null;
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/views/event-mini-view.fxml"));
            secondPane = loader.load();
            eventMiniController2 = loader.getController();
            eventMiniController2.setServices(eventService,userService);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        Pane thirdPane = new Pane();
        EventMiniController eventMiniController3 = null;
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/views/event-mini-view.fxml"));
            thirdPane = loader.load();
            eventMiniController3 = loader.getController();
            eventMiniController3.setServices(eventService,userService);
        }
        catch(IOException e) {
            e.printStackTrace();
        }


        int count = 0;
        for(RoseEvent event: events){
            String imageUrl = event.getEventUrl();
            User organiser = userService.findUserById(event.getOrganiser());
            LocalDateTime date =event.getDate();
            String location = "Location: " + event.getLocation();
            String eventName = event.getEventName();
            if(count == 0) {
                eventsPane.add(firstPane, 0, 0, 1, 1);
                eventMiniController.setContent(imageUrl,organiser,location,eventName,date);
            }
            else if(count == 1) {
                eventsPane.add(secondPane, 0, 1, 1, 1);
                eventMiniController2.setContent(imageUrl,organiser,location,eventName,date);
            }
            else if(count == 2) {
                eventMiniController3.setContent(imageUrl,organiser,location,eventName,date);
                eventsPane.add(thirdPane, 0, 2, 1, 1);
            }
            count++;
        }
        return eventsPane;
    }

    public void initEventsView(){
        eventService.setPageSize(3);
        int pageNumber = eventService.numberOfPagesForEvents(loggedUser.getId());
        if(pageNumber == 0){
            VBox emptyPage = new VBox();
            emptyPage.setLayoutY(270);
            emptyPage.setPrefWidth(659);
            AnchorPane emptyPosts = new AnchorPane();
            emptyPosts.setPrefHeight(260);
            emptyPosts.setPrefWidth(660);
            Label label = new Label("There are no upcoming events!");
            label.setFont(Font.font(26));
            VBox.setMargin(label, new Insets(0, 0, 0, 150));
            emptyPage.getChildren().addAll(emptyPosts, label);
            parentBorderPane.getChildren().add(emptyPage);
        }
        else {
            Pagination pagination = new Pagination(pageNumber, 0);
            pagination.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer pageIndex) {
                    if (pageIndex >= pageNumber) {
                        return null;
                    } else {
                        return createPage(pageIndex);
                    }
                }
            });
            parentBorderPane.setCenter(pagination);
        }
    }

}