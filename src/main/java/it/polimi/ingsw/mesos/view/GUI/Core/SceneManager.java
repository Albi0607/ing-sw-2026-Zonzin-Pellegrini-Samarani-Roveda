package it.polimi.ingsw.mesos.view.GUI.Core;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.LobbyController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class SceneManager {
    private final Stage stage;
    private final GUI gui;
    private ClientController clientController;
    private LobbyController lobbyController;
    private final ObservableGameModel gameModel;

    public SceneManager(Stage stage, GUI gui, ObservableGameModel gameModel) {
        this.stage = stage;
        this.gui = gui;
        this.gameModel = gameModel;
    }

    public void loadLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScene.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setController(gui);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NELL'APERTURA DELL' INTERFACCIA DI LOGIN");
        }
    }


    public void loadLobbyScene(ClientController clientController) {
        try {
            this.clientController = clientController;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lobbyScene.fxml"));
            Parent root = loader.load();
            lobbyController = loader.getController();
            lobbyController.setController(gui,clientController, this);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NELL'APERTURA DELLA LOBBY: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadWaitingRoom() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/waitingRoomScene.fxml"));
            Parent root = loader.load();

            lobbyController = null;
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA WAITINGROOM: " + e.getMessage());
            e.printStackTrace();
        }

        //rendere il lobbyController = null per non ricevere più aggiornamenti
    }

    public void loadGameScene() {
    }

    public void updateLobby(List<LobbyInfoDTO> lobby) {
        if (lobbyController != null) {
            lobbyController.updateLobby(lobby);
            System.out.println("LOBBY UPDATE");
        }
    }
}
