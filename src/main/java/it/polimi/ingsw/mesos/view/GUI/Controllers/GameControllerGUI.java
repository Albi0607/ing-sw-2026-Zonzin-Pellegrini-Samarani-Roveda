package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.common.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.common.ClientModel.OfferTileDTO;
import it.polimi.ingsw.mesos.common.ClientModel.TurnOrderSlotDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.Board.*;
import it.polimi.ingsw.mesos.view.GUI.Controllers.PlayerBoard.PlayerBoardController;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main controller for the game scene.
 * Orchestrates all sub-controllers for the board, rows, offer tiles, turn order,
 * deck and player boards. Binds the ObservableGameModel to the UI so all components
 * update automatically when the server sends new data.
 * Also manages the action label and player turn feedback.
 */
public class GameControllerGUI {

    /** The client controller used to send game actions to the server. */
    private ClientController clientController;
    /** The scene manager used to trigger scene transitions. */
    private SceneManager sceneManager;
    /** The observable game model this controller is bound to. */
    private ObservableGameModel gameModel;
    /** The local player's nickname, used to determine turn ownership. */
    private String localNickname;
    /**
     * Flag used to unit multiple rapid model changes into a single UI refresh.
     * When true, a refresh is already scheduled and further requests are ignored.
     */
    private boolean refreshFlag = false;
    /**
     * When true, the action label is locked and will not be overwritten by state updates.
     * Used to keep action accepted or rejected messages visible for a few seconds.
     */
    private boolean labelLocked = false;


    /** Controller for the upper card row. */
    private TopRowController topRowController;
    /** Controller for the lower card row. */
    private BottomRowController bottomRowController;
    /** Controller for the offer tile area. */
    private OfferTileController offerTileController;
    /** Controller for the turn order track. */
    private TurnOrderController turnOrderController;
    /** Controller for the deck slot. */
    private DeckController deckController;
    /** Controller for the local player's board panel. */
    private PlayerBoardController mainPlayerBoardController;
    /** List of all player board controllers, including the local player. */
    private List<PlayerBoardController> playersController;

    /** Label displaying the current action instruction or feedback to the local player. */
    private Label actionLabel;
    /** Label displaying the current round number. */
    private Label roundLabel;
    /** Label displaying the current era. */
    private Label eraLabel;

    // FXML components
    @FXML private VBox centerBoard;
    @FXML private AnchorPane rootPane;
    @FXML private StackPane bottomPlayerContainer;
    @FXML private StackPane topLeftPlayerContainer;
    @FXML private StackPane topRightPlayerContainer;
    @FXML private StackPane topCenterPlayerContainer;
    @FXML private Pane leftPlayerContainer;
    @FXML private Pane rightPlayerContainer;

    @FXML private StackPane topRightPlayerArea;
    @FXML private StackPane topCenterPlayerArea;
    @FXML private StackPane topLeftPlayerArea;

    @FXML private Pane leftPlayerArea;
    @FXML private Pane rightPlayerArea;

    /**
     * Injects all dependencies and initializes the board, player panels and model bindings.
     * Schedules an initial UI refresh to ensure all nodes are rendered
     * before interactivity is enabled.
     *
     * @param clientController the client controller used to send game actions
     * @param sceneManager the scene manager used for scene transitions
     * @param model the observable game model to bind to
     * @param nickname the local player's nickname
     */
    public void setController(ClientController clientController,SceneManager sceneManager,ObservableGameModel model,String nickname) {
        this.clientController = clientController;
        this.sceneManager = sceneManager;
        this.gameModel = model;
        this.localNickname = nickname;
        initBoard();
        initPlayerBoard();
        bindModel();
        //faccio runLater cosi aspetto che finisca di creare la visualizzazione e poi aggiorno e rendo able i valori
        Platform.runLater(() -> {
            turnOrderController.update(gameModel.getTurnOrderTrack());
            offerTileController.update(gameModel.getOfferTiles());
            refreshUI();
        });
    }

    /**
     * Loads and assembles all subcomponents of the central game board.
     * Creates the top row, deck, turn order track, offer tiles, bottom row,
     * info labels and action label, and arranges them in the center VBox.
     */
    private void initBoard() {
        try {

            centerBoard.setScaleX(0.9);
            centerBoard.setScaleY(0.9);
            centerBoard.setTranslateX(-30);
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
            offerTileController.setController(clientController,this);

            //creo la fila inferiore delle carte che possono essere pescate dai giocatori
            FXMLLoader bottomLoader = new FXMLLoader(getClass().getResource("/fxml/bottomRowCards.fxml"));
            Parent bottom = bottomLoader.load();
            bottomRowController = bottomLoader.getController();
            bottomRowController.setController(clientController,this);

            //creo un VBox per potere mettere alla sinistra del deck le informazioni sul round e sull' era
            roundLabel = new Label();
            eraLabel = new Label();
            VBox infoBox = new VBox(roundLabel,eraLabel);
            infoBox.setSpacing(20);
            infoBox.setAlignment(Pos.CENTER);

            //creo un HBox(allineamento orizzontale) per tenere sullo stesso piano deck, turnOrderTrack e offerTiles
            HBox middleRow = new HBox(infoBox,deck, turn, offer);
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

    /**
     * Creates and places a player board panel for each player.
     * The local player is placed at the bottom without rotation.
     * Other players are assigned to slots based on total player count,
     * and rotated or scaled to fit their position around the board.
     */
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

        List<Pane> usedSlots = switch(players.size()){
            case 2 -> List.of(topCenterPlayerContainer);
            case 3 -> List.of(topLeftPlayerContainer, topRightPlayerContainer);
            case 4 -> List.of(leftPlayerContainer, topCenterPlayerContainer, rightPlayerContainer);
            case 5 -> List.of(leftPlayerContainer, topLeftPlayerContainer, topRightPlayerContainer, rightPlayerContainer);
            default -> {
                System.out.println("ERRORE NELL'ASSEGNAZIONE DEGLI SLOT PLAYER DA USARE");
                yield List.of();}
        };

        // player corrente in basso — nessuna rotazione, nessun wrapper
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/playerBoardView.fxml"));
            Parent view = loader.load();

            PlayerBoardController controller = loader.getController();
            controller.setPlayer(localPlayer,clientController,true);
            playersController.add(controller);
            mainPlayerBoardController = controller;
            bottomPlayerContainer.getChildren().setAll(view);
        } catch (Exception e) {
            System.out.println("ERRORE NELLA CREAZIONE DELLA BOARD DEL PLAYER CORRENTE" + e.getMessage());
            e.printStackTrace();;
        }


        //assegno tutti gli altri player agli slot stabiliti
        for(int i = 0; i<others.size(); i++){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/playerBoardView.fxml"));
                Parent view = loader.load();

                PlayerBoardController controller = loader.getController();
                controller.setPlayer(others.get(i),clientController,false);
                playersController.add(controller);


                Pane targetSlot = usedSlots.get(i);

                if(targetSlot == topCenterPlayerContainer) {

                    view.setRotate(180);
                    topCenterPlayerArea.setManaged(true);
                    targetSlot.getChildren().setAll(view);
                    controller.setHeaderFlipped();

                } else if (targetSlot == topLeftPlayerContainer) {
                    view.setRotate(180);
                    topLeftPlayerArea.setManaged(true);
                    targetSlot.getChildren().setAll(view);
                    controller.setHeaderFlipped();

                } else if (targetSlot == topRightPlayerContainer) {
                    view.setRotate(180);
                    topRightPlayerArea.setManaged(true);
                    targetSlot.getChildren().setAll(view);
                    controller.setHeaderFlipped();

                } else if (targetSlot == leftPlayerContainer) {
                    view.setScaleX(0.9);
                    view.setScaleY(0.9);
                    view.setRotate(90);
                    view.setTranslateX(-210.9);
                    view.setTranslateY(203.5);
                    Group group = new Group(view);
                    group.setLayoutX(0);
                    group.setLayoutY(0);
                    leftPlayerContainer.getChildren().setAll(group);

                } else if (targetSlot == rightPlayerContainer) {
                    view.setScaleX(0.9);
                    view.setScaleY(0.9);
                    view.setRotate(-90);
                    view.setTranslateX(-218.1);
                    view.setTranslateY(203.5);
                    Group group = new Group(view);
                    group.setLayoutX(0);
                    group.setLayoutY(0);
                    rightPlayerContainer.getChildren().setAll(group);
                }

            } catch (Exception e) {
                System.out.println("ERRORE NELLA CREAZIONE DELLE BOARD DEI PLAYER: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Registers listeners on all observable properties and lists of the game model.
     * Each listener triggers either a direct update on the relevant sub-controller
     * or a scheduled UI refresh via scheduledRefresh.
     * Also performs the initial population of all board components.
     */
    private void bindModel() {

        gameModel.getUpperRow().addListener((ListChangeListener<CardDTO>) change ->
                Platform.runLater(() -> topRowController.updateUpper(gameModel.getUpperRow())));

        gameModel.getLowerRow().addListener((ListChangeListener<? super CardDTO>) obs ->
                Platform.runLater(() -> bottomRowController.updateLower(gameModel.getLowerRow())));

        gameModel.getOfferTiles().addListener((ListChangeListener<? super OfferTileDTO>) obs ->
                Platform.runLater(() -> offerTileController.update(gameModel.getOfferTiles())));

        gameModel.getTurnOrderTrack().addListener((ListChangeListener<? super TurnOrderSlotDTO>) obs ->
                Platform.runLater(() -> turnOrderController.update(gameModel.getTurnOrderTrack())));

        gameModel.eraProperty().addListener((o, oldV, newV) -> Platform.runLater(() -> {
            updateEraLabel(newV);
            deckController.updateDeckView(newV);
        }));

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

        //per pescata extra
        gameModel.extraDrawPhaseProperty().addListener((o, oldV, newV) -> {
            scheduledRefresh();
        });

        //fatto alla prima volta in cui setto i parametri ma li creo anche nella UI
        topRowController.updateUpper(gameModel.getUpperRow());
        bottomRowController.updateLower(gameModel.getLowerRow());
        offerTileController.init(gameModel.getOfferTiles());
        turnOrderController.update(gameModel.getTurnOrderTrack());
        deckController.updateDeckView(gameModel.getEra());

    }


    /**
     * Schedules a single UI refresh via Platform.runLater.
     * Uses a flag to ensure that multiple rapid model changes result in only one refresh call.
     */
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

    /**
     * Refreshes all interactive and informational UI components.
     * Checks if the game has ended, updates row and offer tile interactivity,
     * updates the era and round labels, the action label and the skip button.
     * The skip button is updated only for the main player and only if it is
     * currently their turn during an extra draw phase.
     */
    private void refreshUI() {
        checkIfEnd();
        topRowController.refreshInteraction();
        bottomRowController.refreshInteraction();
        offerTileController.refreshOfferTileInteraction();
        updateEraLabel(gameModel.getEra());
        updateRoundLabel(gameModel.getCurrentRound());
        updateActionLabel(gameModel.getGameState());

        //aggiorna il bottone skip solo sul player principale
        if (mainPlayerBoardController!=null) {
            mainPlayerBoardController.updateSkipButton(gameModel.getGameState(),
                    gameModel.isExtraDrawPhase(),
                    isMyTurn(GameState.RESOLVING_ACTIONS)
            );
        }
    }


    /**
     * Updates the action label text and color based on the current game state.
     * If the label is locked by a temporary message, does nothing.
     * Shows whose turn it is and what action is expected.
     *
     * @param state the current game state
     */
    public void updateActionLabel(GameState state) {
        if(labelLocked){
            return;
        }

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

    /**
     * Updates the round label text and applies the standard round label style.
     *
     * @param round the current round number to display
     */
    public void updateRoundLabel(int round) {
        roundLabel.setText("ROUND: " + round);
        UIEffects.applyRoundLabelStyle(roundLabel);
    }

    /**
     * Updates the era label text and applies the standard era label style.
     *
     * @param era the current era string to display
     */
    public void updateEraLabel(String era) {
        eraLabel.setText("ERA: " + era);
        UIEffects.applyEraLabelStyle(eraLabel);
    }

    /**
     * Returns whether it is currently the local player's turn in the given game state.
     *
     * @param state the game state to check against
     * @return true if the local player is the current player and the state matches
     */
    public boolean isMyTurn(GameState state){
        return(localNickname!=null &&
                gameModel.getCurrentPlayerNickname()!=null &&
                gameModel.getCurrentPlayerNickname().equals(localNickname) &&
                gameModel.getGameState()==state);
    }

    /**
     * Returns whether the upper row is currently the active board row.
     *
     * @return true if the upper row is active
     */
    public boolean getIsUpper(){
        return gameModel.getIsUpper();
    }

    /**
     * Returns the current round number from the game model.
     *
     * @return the current round
     */
    public int getCurrentRound(){
        return gameModel.getCurrentRound();
    }


    /**
     * Checks if the game has reached the FINISHED state and triggers the end game scene.
     */
    public void checkIfEnd(){
        if(gameModel.getGameState()==GameState.FINISHED){
            sceneManager.loadEndScene();
        }
    }

    /**
     * Displays a temporary action result message on the action label.
     * Locks the label for a short duration so the message remains visible.
     * Accepted actions are shown for 1 second, rejected actions for 3 seconds.
     * After the timer expires, the label returns to showing the current game state.
     *
     * @param message the message to display
     * @param accept true if the action was accepted, false if rejected
     */
    public void setActionMessage(String message, boolean accept){

        actionLabel.setText(message);
        actionLabel.setTextFill(accept ? Color.GREEN : Color.RED);
        actionLabel.setVisible(true);

        labelLocked = true;

        //metto dei timer in modo che il messaggio di buon riuscita o meno rimane visibile per qualche secondo
        int time = accept ? 1 : 3;

        PauseTransition pause = new PauseTransition(Duration.seconds(time));
        pause.setOnFinished(e -> {
            labelLocked = false;
            updateActionLabel(gameModel.getGameState());
        });

        pause.play();
    }

    /**
     * Displays a generic message on the action label in orange.
     * Used for non-critical notifications that do not lock the label.
     *
     * @param message the message to display
     */
    public void showMessage(String message){
        actionLabel.setText(message);
        actionLabel.setTextFill(Color.ORANGE);
        actionLabel.setVisible(true);
    }

}