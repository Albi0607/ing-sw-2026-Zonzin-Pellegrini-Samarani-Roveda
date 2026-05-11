package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Core.GUI;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameCardController {
    private LobbyInfoDTO data;
    private GUI gui;
    private ClientController clientController;
    private SceneManager sceneManager;


    @FXML private Label roomIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label playersLabel;
    @FXML private Button joinButton;


    @FXML public void initialize(){
        // effetto click
        joinButton.setOnMousePressed(e -> {
            joinButton.setScaleX(0.95);
            joinButton.setScaleY(0.95);
        });

        joinButton.setOnMouseReleased(e -> {
            joinButton.setScaleX(1.0);
            joinButton.setScaleY(1.0);
        });

    }

    public void setData(LobbyInfoDTO dto, GUI gui, ClientController controller, SceneManager sceneManager) {
        this.data = dto;
        this.gui = gui;
        this.clientController = controller;
        this.sceneManager = sceneManager;

        roomIdLabel.setText(String.valueOf(dto.id));
        if(dto.started){
            statusLabel.setText("PARTITA INIZIATA");
        }
        else{
            statusLabel.setText("IN ATTESA DI GIOCATORI");
        }
        playersLabel.setText(dto.numPlayers + "/" + dto.maxNumPlayers);
    }

    @FXML
    public void handleJoinGame() {
        clientController.joinGame(data.id);
        sceneManager.loadWaitingRoom();
    }
}