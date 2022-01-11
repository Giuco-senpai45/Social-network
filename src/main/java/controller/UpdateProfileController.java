package controller;

import controller.pages.PageObject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Callback;
import main.domain.User;
import main.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UpdateProfileController {

    @FXML
    private AnchorPane root;

    @FXML
    private Pagination pagination;

    private PageObject pageObject;

    public void setController(PageObject pageObject){
        this.pageObject = pageObject;
        setPages();
    }

    private AnchorPane createPage(Integer pageIndex){
        AnchorPane mainUpdatePanel = new AnchorPane();
        mainUpdatePanel.setPrefWidth(315);
        mainUpdatePanel.setPrefHeight(260);
        switch(pageIndex){
            case 0:{
                HBox name = new HBox();
                TextField firstName = new TextField();
                firstName.setText(pageObject.getLoggedUser().getFirstName());
                firstName.setMaxWidth(210);
                HBox.setMargin(firstName, new Insets(10, 0, 10, 0));
                TextField lastName = new TextField();
                lastName.setText(pageObject.getLoggedUser().getLastName());
                lastName.setMaxWidth(210);
                HBox.setMargin(lastName, new Insets(10, 20, 10, 0));
                name.getChildren().addAll(firstName, lastName);


                VBox firstUpdatePanel = new VBox();
                TextField address = new TextField();
                address.setText(pageObject.getLoggedUser().getAddress());
                address.setMinWidth(430);
                VBox.setMargin(address, new Insets(10, 0, 10, 0));
                TextField email = new TextField();
                email.setText(pageObject.getLoggedUser().getEmail());
                email.setMaxWidth(210);
                TextField password = new TextField();
                password.setText("");
                password.setMaxWidth(210);
                firstUpdatePanel.getChildren().addAll(name, address, email, password);
                mainUpdatePanel.getChildren().add(firstUpdatePanel);
                break;
            }
            case 1:{
                HBox birthDate = new HBox();
                Label birthLabel = new Label("Birthdate");
                ComboBox day = new ComboBox();
                ComboBox month = new ComboBox();
                ComboBox year = new ComboBox();
                setComboBoxes(day, month, year);
                birthDate.getChildren().addAll(birthLabel, day, month, year);

                HBox gender = new HBox();
                Label genderLabel = new Label("Gender");
                RadioButton male = new RadioButton("Male");
                RadioButton female = new RadioButton("Female");
                RadioButton other = new RadioButton("Other");
                ToggleGroup toggleGroup = new ToggleGroup();
                male.setToggleGroup(toggleGroup);
                female.setToggleGroup(toggleGroup);
                other.setToggleGroup(toggleGroup);
                gender.getChildren().addAll(genderLabel, male, female, other);

                HBox relationship = new HBox();
                Label relationshipLabel = new Label("Relationship status");
                ComboBox relationshipStatus = new ComboBox();
                setComboBox(relationshipStatus);
                relationship.getChildren().addAll(relationshipLabel, relationshipStatus);

                VBox secondUpdatePanel = new VBox();
                secondUpdatePanel.getChildren().addAll(birthDate, gender, relationship);
                mainUpdatePanel.getChildren().add(secondUpdatePanel);
                break;
            }
        }
        return mainUpdatePanel;
    }

    public void setPages(){
        root.getChildren().remove(pagination);
        pagination = new Pagination(3, 0);
        pagination.setLayoutX(219);
        pagination.setLayoutY(118);
        pagination.prefHeight(293);
        pagination.prefWidth(315);
//        BackgroundImage myBI = new BackgroundImage(new Image("/imgs/roses_bkg.png", 660, 260, false, true),
//                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                    BackgroundSize.DEFAULT);
//        pagination.setBackground(new Background(myBI));
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= 3) {
                    return null;
                } else {
                    return createPage(pageIndex);
                }
            }
        });
        root.getChildren().add(pagination);
    }

    public void setComboBoxes(ComboBox day, ComboBox month, ComboBox year){
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

    private void setComboBox(ComboBox relationshipStatus){
        List<String> status = new ArrayList<>();
        status.add("Single");
        status.add("In a relationship");
        status.add("Engaged");
        status.add("It's complicated");
        status.add("Married");
        status.add("Something else");
        status.add("I prefer not to say");
        relationshipStatus.setItems(FXCollections.observableArrayList(status));
    }
}
