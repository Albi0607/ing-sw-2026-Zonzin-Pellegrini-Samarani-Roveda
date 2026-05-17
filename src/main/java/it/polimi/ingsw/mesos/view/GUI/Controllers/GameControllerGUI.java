package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.OfferTileDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.TurnOrderSlotDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.Board.*;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class GameControllerGUI {

    private ClientController clientController;
    private SceneManager sceneManager;
    private ObservableGameModel gameModel;
    private String localNickname;
    private boolean refreshFlag = false;

    private TopRowController topRowController;
    private BottomRowController bottomRowController;
    private OfferTileController offerTileController;
    private TurnOrderController turnOrderController;
    private DeckController deckController;
    private PlayersManagerController playersController;

    @FXML private VBox centerBoard;
    //label creata per spiegare al client tramite visualizzazione di messaggi cosa deve fare
    @FXML private Label actionLabel;


    public void setController(ClientController clientController,SceneManager sceneManager,ObservableGameModel model,String nickname) {
        this.clientController = clientController;
        this.sceneManager = sceneManager;
        this.gameModel = model;
        this.localNickname = nickname;
        initBoard();
        bindModel();
        //faccio runLater cosi aspetto che finisca di creare la visualizzazione e poi aggiorno e rendo able i valori
        Platform.runLater(this::refreshUI);
    }

    //inizializzo la board centrale (senza mani dei giocatori)
    private void initBoard() {
        try {

            //creo la fila superiore delle carte che possono essere pescate dai giocatori
            FXMLLoader topLoader = new FXMLLoader(getClass().getResource("/fxml/topRowCards.fxml"));
            Parent top = topLoader.load();
            topRowController = topLoader.getController();
            topRowController.setController(clientController,this);

            //creo il posto per il deck: il dorso delle carte con l'era corretta
            FXMLLoader deckLoader = new FXMLLoader(getClass().getResource("/fxml/deckArea.fxml"));
            Parent deck = deckLoader.load();
            deckController = deckLoader.getController();

            //creo il turnOrderTrack che dovrà gestire lo spostamento dei totem
            FXMLLoader turnLoader = new FXMLLoader(getClass().getResource("/fxml/turnOrderTrack.fxml"));
            Parent turn = turnLoader.load();
            turnOrderController = turnLoader.getController();

            //creo offerTile che dovrà essere cliccabile e i totem potranno starci sopra
            FXMLLoader offerLoader = new FXMLLoader(getClass().getResource("/fxml/offerTileArea.fxml"));
            Parent offer = offerLoader.load();
            offerTileController = offerLoader.getController();
            offerTileController.set(clientController,this);
            offerTileController.setModel(gameModel);

            //creo la fila inferiore delle carte che possono essere pescate dai giocatori
            FXMLLoader bottomLoader = new FXMLLoader(getClass().getResource("/fxml/bottomRowCards.fxml"));
            Parent bottom = bottomLoader.load();
            bottomRowController = bottomLoader.getController();
            bottomRowController.setController(clientController,this);

            //creo un HBox(allineamento orizzontale) per tenere sullo stesso piano deck, turnOrderTrack e offerTiles
            HBox middleRow = new HBox(deck, turn, offer);
            middleRow.setSpacing(20);
            middleRow.setAlignment(javafx.geometry.Pos.CENTER);

            //metto nel container centrale per la board tutte le cose appena create
            centerBoard.getChildren().setAll(top, middleRow, bottom);

        } catch (IOException e) {
            System.out.println("ERRORE NELL'INIZIALIZZAZIONE DELLA BOARD: "+ e.getMessage());
            e.printStackTrace();
        }
    }

    //associo al gameModel degli osservatori che chiamano i metodi di update quando ricevono un cambiamento
    private void bindModel() {

        gameModel.getUpperRow().addListener((ListChangeListener<CardDTO>) change -> {
            topRowController.updateUpper(gameModel.getUpperRow());
        });

        gameModel.getLowerRow().addListener((ListChangeListener<? super CardDTO>) (obs) -> {
            bottomRowController.updateLower(gameModel.getLowerRow());
        });

        gameModel.getOfferTiles().addListener((ListChangeListener<? super OfferTileDTO>) (obs) -> {
            offerTileController.update(gameModel.getOfferTiles());
        });

        gameModel.getTurnOrderTrack().addListener((ListChangeListener<? super TurnOrderSlotDTO>) (obs) -> {
            turnOrderController.update(gameModel.getTurnOrderTrack());
        });

        /*gameModel.getPlayers().addListener((ListChangeListener<? super ObservablePlayerModel>) (obs) -> {
            playersController.update(gameModel.getPlayers());
        });*/

        gameModel.eraProperty().addListener((o, oldV, newV) -> {
            deckController.updateDeckView(newV);
        });

        gameModel.currentRoundProperty().addListener((o, oldV, newV) -> {
            GameControllerGUI.updateRound(newV.intValue());
            scheduledRefresh();
        });


        //variazioni di stato chiama azioni
        gameModel.gameStateProperty().addListener((o, oldV, newV) -> {
            scheduledRefresh();
        });

        //variazioni di currentPlayer chiama azioni
        gameModel.currentPlayerNicknameProperty().addListener((o, oldV, newV) -> {
            scheduledRefresh();
        });

        //variazione di isUpper nel caso di pescate di piu carte
        gameModel.isUpperProperty().addListener((o, oldV, newV) -> {
            scheduledRefresh();
        });

        //fatto alla prima volta in cui setto i parametri ma li creo anche nella UI
            topRowController.updateUpper(gameModel.getUpperRow());
            bottomRowController.updateLower(gameModel.getLowerRow());
            offerTileController.init(gameModel.getOfferTiles());
            turnOrderController.update(gameModel.getTurnOrderTrack());
            //chiamata del metodo la prima volta cosi che aggiorni i valori fino al prossimo aggiornamento
            deckController.updateDeckView(gameModel.getEra());

    }

    public boolean isMyTurn(GameState state){
        return(localNickname!=null &&
                gameModel.getCurrentPlayerNickname()!=null &&
                gameModel.getCurrentPlayerNickname().equals(localNickname) &&
                gameModel.getGameState()==state);
    }

    //utilizzo questo metodo e il flag cosi che faccio un solo refresh anche se cambiano più parametri
    public void scheduledRefresh(){
        if(refreshFlag){
            return;
        }
        refreshFlag=true;
        Platform.runLater(()->{
            try {
                refreshUI();
            } finally {
                refreshFlag = false;
            }
        });
    }


    private void refreshUI() {
        topRowController.refreshInteraction();
        bottomRowController.refreshInteraction();
        offerTileController.refreshOfferTileInteraction();
        updateActionLabel(gameModel.getGameState());
    }


    //scrive nella label le azioni da fare in base allo stato del gioco
    public void updateActionLabel(GameState state) {

        boolean myTurn = isMyTurn(state);

        if (!myTurn) {
            actionLabel.setText("Turno avversario...");
            actionLabel.setTextFill(Color.RED);
            actionLabel.setVisible(true);
            return;
        }

        switch (state) {

            case PLACING_TOTEMS -> {
                actionLabel.setText("È il tuo turno: piazza un totem");
                actionLabel.setTextFill(Color.GREEN);
            }

            case RESOLVING_ACTIONS -> {
                if (gameModel.getIsUpper()){
                    actionLabel.setText("È il tuo turno: pesca una carta dall'alto");
                    actionLabel.setTextFill(Color.GREEN);
                } else{
                actionLabel.setText("È il tuo turno: pesca una carta dal basso");
                    actionLabel.setTextFill(Color.GREEN);
                }
            }

            default -> {
                actionLabel.setText("");
                actionLabel.setVisible(false);
            }
        }

        actionLabel.setVisible(true);
    }

    public boolean getIsUpper(){
        return gameModel.getIsUpper();
    }

    public int getCurrentRound(){
        return gameModel.getCurrentRound();
    }



    private void handleGameState(GameState newV) {
    }

    private static void updateRound(int i) {
    }


    private void initPlayers(){

    }

}