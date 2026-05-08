package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.view.GUI.Core.ObservableGameModel;
import javafx.fxml.FXML;

public class LobbyController {
    private ClientController clientCtrl;
    private ObservableGameModel gameModel;

    public void setController(ClientController clientCtrl, ObservableGameModel gameModel) { }

    @FXML
    public void createGame() { }

    @FXML
    public void joinGame() { }

    public void cleanup() { }
}
