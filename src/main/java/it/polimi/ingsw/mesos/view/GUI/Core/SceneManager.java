package it.polimi.ingsw.mesos.view.GUI.Core;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.EndGame.EndGameController;
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

import static it.polimi.ingsw.mesos.rete.ClientModel.ClientState.END_GAME;

/**
 * Manages all scene transitions for the GUI client.
 * Loads and switches between JavaFX scenes, injects dependencies into controllers,
 * and routes messages and updates to the currently active controller.
 */
public class SceneManager {
    /** The primary JavaFX stage used to display all scenes. */
    private final Stage stage;
    /** The main GUI instance used to inject into controllers that need it. */
    private final GUI gui;
    /** The observable game model shared across all game controllers. */
    private final ObservableGameModel gameModel;
    /** The current client state, used to route messages to the correct controller. */
    private ClientState clientState;
    /** The client controller used to send game actions, set after login. */
    private ClientController clientController = null;
    /** The controller for the login scene, or null if not currently active. */
    private LoginController loginController = null;
    /** The controller for the lobby scene, or null if not currently active. */
    private LobbyController lobbyController = null;
    /** The controller for the totem choice scene, or null if not currently active. */
    private TotemChoiceController totemController = null;
    /** The controller for the main game scene, or null if not currently active. */
    private GameControllerGUI gameControllerGUI = null;
    /** The controller for the end game scene, or null if not yet loaded. */
    private EndGameController endGameController;

    /** Leaderboard data received before the end game scene was loaded, applied on load. */
    private List<GameResult> pendingLeaderboard = null;
    /** The local player's position in the pending leaderboard. */
    private int pendingPosition = 0;

    /**
     * Creates a new SceneManager with the given stage, GUI and game model.
     *
     * @param stage the primary JavaFX stage
     * @param gui the main GUI instance
     * @param gameModel the observable game model
     */
    public SceneManager(Stage stage, GUI gui, ObservableGameModel gameModel) {
        this.stage = stage;
        this.gui = gui;
        this.gameModel = gameModel;
    }

    /**
     * Loads and displays the login scene.
     * Injects the GUI into the login controller.
     */
    public void loadLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScene.fxml"));
            Parent root = loader.load();
            this.loginController = loader.getController();
            this.loginController.setController(gui);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NELL'APERTURA DELL' INTERFACCIA DI LOGIN");
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays the lobby scene.
     * Stores the client controller for use by subsequent scenes.
     *
     * @param clientController the client controller to inject into lobby-related controllers
     */
    public void loadLobbyScene(ClientController clientController) {
        try {
            this.clientController = clientController;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lobbyScene.fxml"));
            Parent root = loader.load();
            lobbyController = loader.getController();
            lobbyController.setController(this);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("ERRORE NELL'APERTURA DELLA LOBBY: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays the totem choice scene.
     * Injects the client controller and scene manager, and sets the scene parameters.
     *
     * @param id -1 if creating a new game, the game id if joining
     * @param numPlayers the number of players for the new game, used only in create mode
     * @param dto the lobby data for the game being joined, or null in create mode
     */
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

    /**
     * Loads and displays the waiting room scene.
     * Clears the lobby and totem controllers since they are no longer needed.
     */
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
    }

    /**
     * Loads and displays the main game scene.
     * Injects the client controller, scene manager, game model and local nickname.
     * Clears the lobby and totem controllers since they are no longer needed.
     *
     * @return the GameControllerGUI instance, or null if loading failed
     */
    public GameControllerGUI loadGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameScene.fxml"));
            Parent root = loader.load();
            lobbyController = null;
            totemController = null;
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

    /**
     * Loads and displays the end game scene.
     * If a leaderboard was received before this scene was loaded, applies it immediately.
     */
    public void loadEndScene(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/endGameScene.fxml"));
            Parent root = loader.load();
            endGameController = loader.getController();
            endGameController.setController(gameModel);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            if (pendingLeaderboard != null) {
                endGameController.showLeaderboard(pendingLeaderboard, pendingPosition);
                pendingLeaderboard = null;
            }
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLA FINE DEL GIOCO  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Forwards a lobby update to the active totem controller and lobby controller.
     *
     * @param lobby the updated list of available games
     */
    public void updateLobby(List<LobbyInfoDTO> lobby) {
        if(totemController!=null){
            totemController.updateDTO(lobby);
        }
        if (lobbyController != null) {
            lobbyController.updateLobby(lobby);
        }
    }

    /**
     * Updates the current client state and triggers a scene transition when needed.
     * Loads the end game scene when the state is END_GAME.
     * Loads the waiting room when the state is WAITING_PLAYERS, which covers both
     * the normal post-join flow and the reconnection case where the player was in
     * an abandoned game and needs to wait for it to resume.
     *
     * @param currentState the new client state
     */
    public void updateClientState(ClientState currentState){
        this.clientState = currentState;

        switch (currentState) {
            case END_GAME -> this.loadEndScene();

            case WAITING_PLAYERS -> this.loadWaitingRoom();

            default -> { /* nessuna transizione di scena automatica */ }
        }
    }

    /**
     * Routes a message to the currently active controller based on the client state.
     * In LOBBY state, routes to the lobby or totem controller.
     * In IN_GAME state, routes to the game controller.
     *
     * @param message the message to display
     */
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

    /**
     * Displays the all-time leaderboard on the end game screen.
     * If the end game scene is already loaded, applies immediately.
     * Otherwise, stores the data to be applied when the scene loads.
     *
     * @param leaderboard the list of all-time game results
     * @param position the 1-based position of the local player in the leaderboard
     */
    public void showLeaderboard(List<GameResult> leaderboard, int position) {
        Platform.runLater(() -> {
            if (endGameController != null) {
                // scena già caricata, applica subito
                endGameController.showLeaderboard(leaderboard, position);
            } else {
                // scena non ancora caricata, salva per dopo
                pendingLeaderboard = leaderboard;
                pendingPosition = position;
            }
        });
    }

    /**
     * Returns the login controller if the login scene is currently active.
     *
     * @return the LoginController, or null if not active
     */
    public LoginController getLoginController() {
        return this.loginController;
    }

    /**
     * Returns the lobby controller
     *
     * @return the LobbyController
     */
    public LobbyController getLobbyController(){return this.lobbyController;}
}
