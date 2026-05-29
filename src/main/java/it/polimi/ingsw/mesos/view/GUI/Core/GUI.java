package it.polimi.ingsw.mesos.view.GUI.Core;


import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientChoseSetup;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.rete.View;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame.LoginController;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class GUI extends Application implements View {
    private Stage primaryStage;
    private SceneManager sceneManager;
    private ClientController clientController;
    private ClientState clientState;
    private ObservableGameModel gameModel;
    private GameControllerGUI gameControllerGUI;
    private String localNickname;
    private boolean gameStarted;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        gameModel = new ObservableGameModel();
        sceneManager = new SceneManager(primaryStage, this,clientState, gameModel);
        gameStarted=false;
        sceneManager.loadLoginScene();
        this.primaryStage.setTitle("Mesos");
        this.primaryStage.getIcons().add(new Image("/images/tool/icon.png"));
        this.primaryStage.show();
    }

    @Override
    public void showLastUpdate(GameDTO game) {
       Platform.runLater(() -> {

            //partita appena iniziata aggiorno il modello e cambio la scene con il modello appena aggiornato
            if(!gameStarted&&gameControllerGUI==null) {
                gameStarted = true;
                gameModel.updateFromDTO(game);
                gameControllerGUI = sceneManager.loadGameScene();
                return;
            }

            //partita già iniziata solo aggiornamento del modello
            gameModel.updateFromDTO(game);
        });
    }

    @Override
    public void showMessage(String message) {
        Platform.runLater(() -> {
            sceneManager.showMessage(message);
        });
    }

    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) {
        // DA IMPLEMENTARE: Platform.runLater() -> aggiorna scena lobby
        Platform.runLater(()->{

            if (this.clientState == null || this.clientState != ClientState.LOBBY) {
                this.clientState = ClientState.LOBBY;
                sceneManager.loadLobbyScene(clientController);
            }

            sceneManager.updateLobby(lobby);
        });
    }

    @Override
    public void showClientStateUpdate(ClientState currentState){
        if(this.clientState!=currentState) {
            this.clientState=currentState;
            sceneManager.updateClientState(currentState);
        }
    }

    @Override
    public void showActionRejected(String reason) {
       Platform.runLater(() -> {
            if (gameControllerGUI != null) {
                gameControllerGUI.setActionMessage(reason, false);
            }
        });
    }

    @Override
    public void showActionAccepted(String message) {
       Platform.runLater(() -> {
            if (gameControllerGUI != null) {
                gameControllerGUI.setActionMessage(message, true);
            }
        });
    }

    @Override
    public void  showLoginError(String message) {
       Platform.runLater(() -> {

            LoginController loginController = sceneManager.getLoginController();

            if (loginController != null) {
                loginController.showLoginError(message);
            }
        });
    }

    //capire se gestire qua questa cosa o se fare diversamente, rendere le scelte di IP e PORT veramente utili e usabili
    public void handleLogin(String nickname,String ip,int port,String networkChoice,String clientIp){
        try {
            if (ip == null || ip.trim().isEmpty()) {
                ip = "127.0.0.1";
            }
            Network network = ClientChoseSetup.createNetwork(networkChoice, ip, port,clientIp);
            this.clientController = new ClientController(this, network);
            clientController.getLobby(nickname);
        } catch (Exception e) {
            showMessage("Errore: nel fare l'accesso e/o nell'andare nella lobby");
        }
    }

    public void setNickname(String nickname){
        this.localNickname = nickname;
    }

    public String getNickname(){
        return this.localNickname;
    }

    @Override
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {
        Platform.runLater(()-> sceneManager.showLeaderboard(leaderboard,myPosition));
    }
}
