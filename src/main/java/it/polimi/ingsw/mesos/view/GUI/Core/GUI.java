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

/**
 * Main JavaFX application class and entry point for the GUI client.
 * Implements the View interface to receive updates from the server via the ClientController.
 * Manages the ObservableGameModel and delegates all scene transitions to the SceneManager.
 */
public class GUI extends Application implements View {
    /** The primary JavaFX stage used to display all scenes. */
    private Stage primaryStage;
    /** The scene manager responsible for all scene transitions and UI routing. */
    private SceneManager sceneManager;
    /** The client controller used to send player actions to the server. */
    private ClientController clientController;
    /** The current state of the client, used to route messages to the correct scene. */
    private ClientState clientState;
    /** The observable game model shared between the GUI and all game controllers. */
    private ObservableGameModel gameModel;
    /** The main game controller, created when the first game update is received. */
    private GameControllerGUI gameControllerGUI;
    /** The local player's nickname, set after login. */
    private String localNickname;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the primary stage, creates the observable game model and scene manager,
     * and loads the login scene.
     *
     * @param primaryStage the primary JavaFX stage provided by the framework
     */
    @Override public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        gameModel = new ObservableGameModel();
        sceneManager = new SceneManager(primaryStage, this, gameModel);
        sceneManager.loadLoginScene();
        this.primaryStage.setTitle("Mesos");
        this.primaryStage.getIcons().add(new Image("/images/tool/icon.png"));
        this.primaryStage.show();
    }

    /**
     * Receives the latest game state from the server and updates the observable model.
     * On the first update, creates the game controller and loads the game scene.
     * On subsequent updates, only refreshes the model.
     *
     * @param game the latest GameDTO received from the server
     */
    @Override
    public void showLastUpdate(GameDTO game) {
       Platform.runLater(() -> {

            //partita appena iniziata aggiorno il modello e cambio la scene con il modello appena aggiornato
            if(gameControllerGUI==null) {
                gameModel.updateFromDTO(game);
                gameControllerGUI = sceneManager.loadGameScene();
                return;
            }

            //partita già iniziata solo aggiornamento del modello
            gameModel.updateFromDTO(game);
        });
    }

    /**
     * Displays a message to the player by routing it to the currently active scene.
     *
     * @param message the message to display
     */
    @Override
    public void showMessage(String message) {
        Platform.runLater(() -> {
            sceneManager.showMessage(message);
        });
    }

    /**
     * Receives the current lobby state from the server.
     * If the client is reconnecting to an abandoned game, the call is ignored.
     * Loads the lobby scene only on the first call (when no lobby controller is active yet),
     * then only updates the lobby list on subsequent calls, so players currently in
     * the totem choice screen are not redirected back to the lobby.
     *
     * @param lobby the current list of available games
     */
    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) {
        Platform.runLater(() -> {

            if (this.clientState == ClientState.IN_GAME) {
                return;
            }

            if (sceneManager.getLobbyController() == null) {
                sceneManager.loadLobbyScene(clientController);
            }

            sceneManager.updateLobby(lobby);
        });
    }

    /**
     * Receives a client state update from the server and notifies the scene manager
     * if the state has changed.
     *
     * @param currentState the new client state
     */
    @Override
    public void showClientStateUpdate(ClientState currentState){
        Platform.runLater(() -> {
            if(this.clientState != currentState) {
                this.clientState = currentState;
                sceneManager.updateClientState(currentState);
            }
        });
    }

    /**
     * Receives a rejected action notification from the server and forwards
     * the reason to the game controller.
     *
     * @param reason the reason the action was rejected
     */
    @Override
    public void showActionRejected(String reason) {
       Platform.runLater(() -> {
            if (gameControllerGUI != null) {
                gameControllerGUI.setActionMessage(reason, false);
            }
        });
    }

    /**
     * Receives an accepted action notification from the server and forwards
     * the message to the game controller.
     *
     * @param message the message describing the accepted action
     */
    @Override
    public void showActionAccepted(String message) {
       Platform.runLater(() -> {
            if (gameControllerGUI != null) {
                gameControllerGUI.setActionMessage(message, true);
            }
        });
    }

    /**
     * Receives a login error from the server and forwards it to the login controller.
     *
     * @param message the error message to display on the login screen
     */
    @Override
    public void  showLoginError(String message) {
       Platform.runLater(() -> {

            LoginController loginController = sceneManager.getLoginController();

            if (loginController != null) {
                loginController.showLoginError(message);
            }
        });
    }

    /**
     * Handles the login action triggered by the login screen.
     * Creates the network connection and the clientController,
     * then requests to enter the lobby with the given nickname.
     *
     * @param nickname the player's chosen nickname
     * @param ip the server IP address
     * @param port the server port
     * @param networkChoice the network protocol chosen by the player (SOCKET or RMI)
     * @param clientIp the client's own IP address, used for RMI
     */
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

    /**
     * Sets the local player's nickname after a successful login.
     *
     * @param nickname the nickname to store
     */
    public void setNickname(String nickname){
        this.localNickname = nickname;
    }

    /**
     * Returns the local player's nickname.
     *
     * @return the local player's nickname
     */
    public String getNickname(){
        return this.localNickname;
    }

    /**
     * Receives the all-time leaderboard from the server and forwards it to the scene manager.
     *
     * @param leaderboard the list of all-time game results
     * @param myPosition the position of the local player in the leaderboard
     */
    @Override
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {
        sceneManager.showLeaderboard(leaderboard,myPosition);
    }
}
