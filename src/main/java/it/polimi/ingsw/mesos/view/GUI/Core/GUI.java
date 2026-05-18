package it.polimi.ingsw.mesos.view.GUI.Core;


import it.polimi.ingsw.mesos.rete.ClientChoseSetup;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.rete.View;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class GUI extends Application implements View {
    private Stage primaryStage;
    private SceneManager sceneManager;
    private ClientController clientController;
    private ClientState clientState;
    private ObservableGameModel gameModel;
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
        javafx.application.Platform.runLater(() -> {

            //partita appena iniziata aggiorno il modello e cambio la scene con il modello appena aggiornato
            if(!gameStarted) {
                gameStarted = true;
                gameModel.updateFromDTO(game);
                sceneManager.loadGameScene();
                return;
            }

            //partita già iniziata solo aggiornamento del modello
            gameModel.updateFromDTO(game);
        });
    }

    @Override
    public void showMessage(String message) {
        // DA IMPLEMENTARE: Platform.runLater() -> mostra alert o toast
    }

    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) {
        // DA IMPLEMENTARE: Platform.runLater() -> aggiorna scena lobby
        javafx.application.Platform.runLater(()->{
            sceneManager.updateLobby(lobby);
        });
    }

    @Override
    public void showClientStateUpdate(ClientState currentState){
        this.clientState = currentState;
    }

    @Override
    public void showActionRejected(String reason) {
    }

    @Override
    public void showActionAccepted(String message) {
    }

    @Override
    public void  showLoginError(String message) {
    }

    //capire se gestire qua questa cosa o se fare diversamente, rendere le scelte di IP e PORT veramente utili e usabili
    public void handleLogin(String nickname,String ip,int port,String networkChoice){
        try {
            if (ip == null || ip.trim().isEmpty()) {
                ip = "127.0.0.1";
            }
            Network network = ClientChoseSetup.createNetwork(networkChoice, ip, port);
            this.clientController = new ClientController(this, network);
            sceneManager.loadLobbyScene(clientController);
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

}
