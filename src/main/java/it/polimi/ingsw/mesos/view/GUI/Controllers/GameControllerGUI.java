package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.OfferTileDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.TurnOrderSlotDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.Board.*;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private List<PlayerBoardController> playersController;

    @FXML private VBox centerBoard;
    //label creata per spiegare al client tramite visualizzazione di messaggi cosa deve fare
    private Label actionLabel;
    @FXML private StackPane bottomPlayerContainer;
    @FXML private StackPane topLeftPlayerContainer;
    @FXML private StackPane topRightPlayerContainer;
    @FXML private StackPane topCenterPlayerContainer;
    @FXML private StackPane leftPlayerContainer;
    @FXML private StackPane rightPlayerContainer;

    @FXML private StackPane topRightPlayerArea;
    @FXML private StackPane topCenterPlayerArea;
    @FXML private StackPane topLeftPlayerArea;



    public void setController(ClientController clientController,SceneManager sceneManager,ObservableGameModel model,String nickname) {
        this.clientController = clientController;
        this.sceneManager = sceneManager;
        this.gameModel = model;
        this.localNickname = nickname;
        initBoard();
        initPlayerBoard();
        bindModel();
        //faccio runLater cosi aspetto che finisca di creare la visualizzazione e poi aggiorno e rendo able i valori
        Platform.runLater(this::refreshUI);
    }

    //inizializzo la board centrale (senza mani dei giocatori)
    private void initBoard() {
        try {

            centerBoard.setScaleX(0.9);
            centerBoard.setScaleY(0.9);
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
            turnOrderController.setTurnOrder(gameModel.getTurnOrderTrack());

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
            middleRow.setAlignment(Pos.CENTER);
            actionLabel = new Label();
            actionLabel.setTextFill(Color.GREEN);
            actionLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            actionLabel.setAlignment(Pos.CENTER);
            actionLabel.setTextAlignment(TextAlignment.CENTER);
            actionLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(actionLabel, Priority.ALWAYS);

            //metto nel container centrale per la board tutte le cose appena create
            centerBoard.getChildren().setAll(top, middleRow, bottom,actionLabel);

        } catch (IOException e) {
            System.out.println("ERRORE NELL'INIZIALIZZAZIONE DELLA BOARD: "+ e.getMessage());
            e.printStackTrace();
        }
    }

    //creo le postazioni dei player in base al loro numero
    private void initPlayerBoard(){
        topRightPlayerArea.setManaged(false);
        topCenterPlayerArea.setManaged(false);
        topLeftPlayerArea.setManaged(false);


        playersController = new ArrayList<>();
        List<ObservablePlayerModel> players = gameModel.getPlayers();

        ObservablePlayerModel localPlayer = players.stream()
                .filter(p -> p.getNickname().equals(localNickname))
                .findFirst()
                .orElse(null);

        List<ObservablePlayerModel> others = players.stream()
                .filter(p -> !p.getNickname().equals(localNickname))
                .toList();

        List<StackPane> usedSlots = switch(players.size()){
            case 2 -> List.of(topCenterPlayerContainer);

            case 3 -> List.of(topLeftPlayerContainer, topRightPlayerContainer);

            case 4 -> List.of(leftPlayerContainer, topCenterPlayerContainer, rightPlayerContainer);

            case 5 -> List.of(leftPlayerContainer, topLeftPlayerContainer, topRightPlayerContainer, rightPlayerContainer);

            default -> {
                System.out.println("ERRORE NELL'ASSEGNAZIONE DEGLI SLOT PLAYER DA USARE");
                yield List.of();}
        };

        //assegno il player corrente al bottomSlot
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/playerBoardView.fxml"));
            Parent view = loader.load();

            PlayerBoardController controller = loader.getController();
            controller.setPlayer(localPlayer);
            playersController.add(controller);
            bottomPlayerContainer.getChildren().setAll(view);
        } catch (Exception e) {
            System.out.println("ERRORE NELLA CREAZIONE DELLE BOARD DEL PLAYER CORRENTE" + e.getMessage());
            e.printStackTrace();;
        }


        //assegno tutti gli altri player agli slot stabiliti
        for(int i = 0; i<others.size(); i++){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/playerBoardView.fxml"));
                Parent view = loader.load();

                PlayerBoardController controller = loader.getController();
                controller.setPlayer(others.get(i));
                playersController.add(controller);


                StackPane targetSlot = usedSlots.get(i);

                //compio le rotazioni cosi che le mani dei giocatori siano settate correttamente
                if(targetSlot == topCenterPlayerContainer) {

                    view.setRotate(180);
                    topCenterPlayerArea.setManaged(true);

                } else if(targetSlot == topLeftPlayerContainer){

                    view.setRotate(180);
                    topLeftPlayerArea.setManaged(true);

                } else if(targetSlot == topRightPlayerContainer){

                    view.setRotate(180);
                    topRightPlayerArea.setManaged(true);

                } else if(targetSlot == leftPlayerContainer){

                    view.setRotate(90);

                } else if(targetSlot == rightPlayerContainer){

                    view.setRotate(-90);

                }

                Group wrapper = new Group(view);

                targetSlot.getChildren().setAll(wrapper);
            } catch (Exception e) {
                System.out.println("ERRORE NELLA CREAZIONE DELLE BOARD DEI PLAYER" + e.getMessage());
                e.printStackTrace();;
            }
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

        gameModel.eraProperty().addListener((o, oldV, newV) -> {
            deckController.updateDeckView(newV);
        });

        gameModel.currentRoundProperty().addListener((o, oldV, newV) -> {
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
        checkIfEnd();
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

    public void checkIfEnd(){
        if(gameModel.getGameState()==GameState.FINISHED){
            Platform.runLater(sceneManager::loadEndScene);
        }
    }



}