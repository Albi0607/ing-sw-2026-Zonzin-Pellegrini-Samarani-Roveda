package it.polimi.ingsw.mesos.view.GUI.Core;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.EndGameController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame.LobbyController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame.LoginController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame.TotemChoiceController;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class SceneManager {
    private final Stage stage;
    private final GUI gui;
    private ClientState clientState;
    private ClientController clientController = null;
    private LoginController loginController = null;
    private LobbyController lobbyController = null;
    private TotemChoiceController totemController = null;
    private GameControllerGUI gameControllerGUI = null;
    private final ObservableGameModel gameModel;
    private EndGameController endGameController;

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

    public GameControllerGUI loadGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameScene.fxml"));
            Parent root = loader.load();
            if(lobbyController!=null){
                lobbyController=null;
            }
            if(totemController!=null){
                totemController=null;
            }
            gameControllerGUI = loader.getController();
            gameControllerGUI.setController(clientController,this,gameModel,gui.getNickname());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            return gameControllerGUI;
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA GIOCO  : " + e.getMessage());
            e.printStackTrace();
            return null;
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
            endGameController = loader.getController();
            endGameController.set(gameModel);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA FINE DEL GIOCO  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateClientState(ClientState currentState){
        this.clientState=currentState;
    }

    public void showMessage(String message){

        if(clientState == ClientState.LOBBY){
            if(lobbyController!=null){
                lobbyController.showMessage(message);
            } else if(totemController!=null){
                totemController.showMessage(message);
            }
        } else if(clientState == ClientState.IN_GAME){
            gameControllerGUI.showMessage(message);
        }

    }

    public void showLeaderboard(List<GameResult> leaderboard, int myPosition){
        endGameController.showLeaderboard(leaderboard,myPosition);
    }

    public LoginController getLoginController() {
        return this.loginController;
    }
}
