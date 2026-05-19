package it.polimi.ingsw.mesos.view.GUI.Core;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.EndGameController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame.LobbyController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame.LoginController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame.TotemChoiceController;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class SceneManager {
    private final Stage stage;
    private final GUI gui;
    private ClientState clientState;
    private ClientController clientController;
    private LoginController loginController;
    private LobbyController lobbyController;
    private TotemChoiceController totemController;
    private final ObservableGameModel gameModel;

    public SceneManager(Stage stage, GUI gui, ClientState clientState, ObservableGameModel gameModel) {
        this.stage = stage;
        this.gui = gui;
        this.clientState = clientState;
        this.gameModel = gameModel;
    }

    public void loadLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScene.fxml"));
            Parent root = loader.load();
            this.loginController = loader.getController();
            this.loginController.setController(gui);
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
            totemController=null;
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA WAITINGROOM: " + e.getMessage());
            e.printStackTrace();
        }

        //rendere il lobbyController = null per non ricevere più aggiornamenti
    }

    public void loadGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameScene.fxml"));
            Parent root = loader.load();
            if(lobbyController!=null){
                lobbyController=null;
            }
            GameControllerGUI controller = loader.getController();
            controller.setController(clientController,this,gameModel,gui.getNickname());
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA GIOCO  : " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void loadTotemScene(int id, int numPlayers,LobbyInfoDTO dto){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/totemScene.fxml"));
            Parent root = loader.load();
            totemController = loader.getController();
            totemController.setController(clientController,this);
            totemController.setParameter(id,numPlayers,dto);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA GIOCO  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateLobby(List<LobbyInfoDTO> lobby) {
        if(totemController!=null){
            totemController.updateDTO(lobby);
            System.out.println("CONTROLLO SULL' AGGIORNAMENTO DELL'INTERFACCIA TOTEM");
        }
        if (lobbyController != null) {
            lobbyController.updateLobby(lobby);
            System.out.println("LOBBY UPDATE");
        }
    }

    public void loadEndScene(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/endGameScene.fxml"));
            Parent root = loader.load();
            EndGameController controller = loader.getController();
            controller.set(gameModel);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA FINE DEL GIOCO  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoginController getLoginController() {
        return this.loginController;
    }
}
