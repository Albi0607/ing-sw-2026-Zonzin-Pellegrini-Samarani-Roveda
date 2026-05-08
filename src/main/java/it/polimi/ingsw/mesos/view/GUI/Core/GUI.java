package it.polimi.ingsw.mesos.view.GUI.Core;


import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.View;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class GUI extends Application implements View {
    private SceneManager sceneManager;
    private ClientController controller;
    private ObservableGameModel gameModel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Inizializzazione SceneManager e caricamento prima scena
    }

    @Override
    public void showLastUpdate(GameDTO game) {
        // DA IMPLEMENTARE: Platform.runLater() -> gameModel.updateFromDTO(game)
    }

    @Override
    public void showMessage(String message) {
        // DA IMPLEMENTARE: Platform.runLater() -> mostra alert o toast
    }

    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) {
        // DA IMPLEMENTARE: Platform.runLater() -> aggiorna scena lobby
    }

    @Override
    public void showClientStateUpdate(ClientState currentState){
    }
}
