package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class EndGameController {
    private ObservableGameModel gameModel;

    @FXML private VBox resultContainer;
    @FXML private Label databaseLabel;
    @FXML private ScrollPane databaseContainer;
    @FXML private VBox databaseVBox;


    public void set(ObservableGameModel gameModel) {
        this.gameModel = gameModel;
        printResult();
    }

    private void printResult(){
        databaseLabel.setManaged(false);
        databaseContainer.setManaged(false);
        databaseVBox.setManaged(false);
        databaseLabel.setDisable(true);
        databaseContainer.setDisable(true);
        databaseVBox.setDisable(true);



    }



}
