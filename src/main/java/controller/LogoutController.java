package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class LogoutController {


    private Stage mainStage;
    private Stage stage;

    public void init(Stage mainStage, Stage stage){
        this.mainStage = mainStage;
        this.stage = stage;
    }

    @FXML
    public void handleYesClicked(ActionEvent actionEvent) {
        stage.close();
        mainStage.close();;
    }

    @FXML
    public void handleNoClicked(ActionEvent actionEvent) {
        stage.close();
    }
}
