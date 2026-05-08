package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.view.GUI.Core.ObservableGameModel;
import javafx.fxml.FXML;

public class GameController {
    private ClientController clientCtrl;
    private ObservableGameModel gameModel;

    // fx:include inietta automaticamente questi sub-controller se i nomi combaciano con gli fx:id
    @FXML private BoardAreaController boardCtrl;
    @FXML private PlayersManagerController playersCtrl;
    @FXML
    private TurnOrderAreaController turnOrderCtrl;

    public void setController(ClientController clientCtrl, ObservableGameModel gameModel) {
        // Salva i riferimenti e poi chiama i metodi init(...) dei sub-controller
    }

    public void cleanup() {
        // Chiama i cleanup dei sub-controller
    }
}
