package it.polimi.ingsw.mesos.view.GUI.Core;

import it.polimi.ingsw.mesos.rete.ClientController;
import javafx.stage.Stage;

public class SceneManager {
    private Stage stage;
    private ClientController clientController;
    private ObservableGameModel gameModel;

    public SceneManager(Stage stage, ClientController clientCtrl, ObservableGameModel model) {
        // ...
    }

    public void loadLoginScene() { }
    public void loadLobbyScene() { }
    public void loadGameScene() { }
}
