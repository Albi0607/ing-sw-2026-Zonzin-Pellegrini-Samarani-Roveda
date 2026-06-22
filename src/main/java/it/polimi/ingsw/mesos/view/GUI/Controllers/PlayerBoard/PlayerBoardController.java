package it.polimi.ingsw.mesos.view.GUI.Controllers.PlayerBoard;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.common.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for a single player's board panel.
 * Displays the player's name, food, prestige points and card collections,
 * grouped by character type and buildings.
 * Binds directly to an ObservablePlayerModel so all scalar values update automatically.
 * Also manages the skip button visibility for the main player during extra draw phases.
 */
public class PlayerBoardController {
    /** The observable player model this board is bound to. */
    private ObservablePlayerModel playerModel;
    /** The client controller used to send game actions such as skip. */
    private ClientController clientController;
    /** Whether this board belongs to the main local player. Used to show the skip button. */
    private boolean isMainPlayer = false;
    /** The CSS color string derived from the player color, used to style labels. */
    private String colorPaint;

    /** Card ids for artist characters owned by this player. */
    private final List<String> artists = new ArrayList<>();
    /** Card ids for builder characters owned by this player. */
    private final List<String> builders = new ArrayList<>();
    /** Card ids for gatherer characters owned by this player. */
    private final List<String> gatherers = new ArrayList<>();
    /** Card ids for hunter characters owned by this player. */
    private final List<String> hunters = new ArrayList<>();
    /** Card ids for inventor characters owned by this player. */
    private final List<String> inventors = new ArrayList<>();
    /** Card ids for shaman characters owned by this player. */
    private final List<String> shamans = new ArrayList<>();

    /** Ordered list of all character type card id lists. */
    List<List<String>> characters;
    /** Ordered list of card slot StackPanes for the six character types. */
    List<StackPane> stackPanes;
    /** Ordered list of card ImageViews for the six character types. */
    List<ImageView> imagesView;
    /** Ordered list of card count labels for the six character types. */
    List<Label> cardsCount;

    // FXML components

    @FXML private HBox headerBox;
    @FXML Label playerNameLabel;
    @FXML Label foodPointsLabel;
    @FXML Label prestigePointsLabel;

    @FXML StackPane cardSlot1;
    @FXML ImageView cardImage1;
    @FXML Label cardCount1;
    @FXML StackPane cardSlot2;
    @FXML ImageView cardImage2;
    @FXML Label cardCount2;
    @FXML StackPane cardSlot3;
    @FXML ImageView cardImage3;
    @FXML Label cardCount3;
    @FXML StackPane cardSlot4;
    @FXML ImageView cardImage4;
    @FXML Label cardCount4;
    @FXML StackPane cardSlot5;
    @FXML ImageView cardImage5;
    @FXML Label cardCount5;
    @FXML StackPane cardSlot6;
    @FXML ImageView cardImage6;
    @FXML Label cardCount6;
    @FXML StackPane cardSlot7;
    @FXML ImageView cardImage7;
    @FXML Label cardCount7;

    @FXML Button skipButton;

    /**
     * Initializes all card slots as hidden and builds the parallel lists
     * for character type slots, images and count labels.
     */
    @FXML
    public void initialize() {

        //in questo modo se non ci sono carte di un tipo non occupo spazio
        disableSlot(cardSlot1);
        disableSlot(cardSlot2);
        disableSlot(cardSlot3);
        disableSlot(cardSlot4);
        disableSlot(cardSlot5);
        disableSlot(cardSlot6);
        disableSlot(cardSlot7);

        characters = List.of(artists, builders, gatherers, hunters, inventors, shamans);
        stackPanes = List.of(cardSlot1, cardSlot2, cardSlot3, cardSlot4, cardSlot5, cardSlot6);
        imagesView = List.of(cardImage1, cardImage2, cardImage3, cardImage4, cardImage5, cardImage6);
        cardsCount = List.of(cardCount1, cardCount2, cardCount3, cardCount4, cardCount5, cardCount6);
    }

    /**
     * Hides a card slot and removes it from the layout flow so it takes no space
     * when no cards of that type have been acquired yet.
     *
     * @param slot the StackPane slot to hide
     */
    private void disableSlot(StackPane slot) {
        slot.setVisible(false);
        slot.setManaged(false);
    }

    /**
     * Binds this board to the given player model and sets up all listeners.
     *
     * @param playerModel the observable player model to bind to
     * @param clientController the client controller used to send game actions
     * @param isMainPlayer true if this board belongs to the local player
     */
    public void setPlayer(ObservablePlayerModel playerModel,ClientController clientController, boolean isMainPlayer) {
        this.playerModel = playerModel;
        this.clientController = clientController;
        this.isMainPlayer = isMainPlayer;

        bind();
    }

    /**
     * Binds all labels to the player model properties and registers listeners
     * for new character and building cards.
     * Label colors are derived from the player's assigned color.
     */
    private void bind() {
        //trova il colore del player e scrive le label con quel colore
        colorPaint = switch (playerModel.getColor()) {
            case BLUE -> "blue";
            case YELLOW -> "yellow";
            case PURPLE -> "purple";
            case WHITE -> "white";
            case RED -> "red";
            default -> "black";
        };

        //aggiorna automaticamente il nome
        playerNameLabel.textProperty().bind(playerModel.nicknameProperty());
        playerNameLabel.setStyle("-fx-text-fill: " + colorPaint + ";" + "-fx-font-size: 22px;" + "-fx-font-weight: bold;");

        //aggiorna automaticamente il cibo
        foodPointsLabel.textProperty().bind(playerModel.foodProperty().asString());
        foodPointsLabel.setStyle("-fx-text-fill: " + colorPaint + ";" + "-fx-font-size: 22px;" + "-fx-font-weight: bold;");

        //aggiorna automaticamente i punti prestigio
        prestigePointsLabel.textProperty().bind(playerModel.prestigePointsProperty().asString());
        prestigePointsLabel.setStyle("-fx-text-fill: " + colorPaint + ";" + "-fx-font-size: 22px;" + "-fx-font-weight: bold;");

        playerModel.getTribe().getCharacters().addListener((ListChangeListener<? super CardDTO>) obs -> {
            List<CardDTO> list = playerModel.getTribe().getCharacters();
            CardDTO newCard = list.get(list.size() - 1);
            addCharacterCard(newCard);
        });

        playerModel.getTribe().getBuildings().addListener((ListChangeListener<? super CardDTO>) obs -> {
            List<CardDTO> list = playerModel.getTribe().getBuildings();
            CardDTO newCard = list.get(list.size() - 1);
            addBuildingCard(newCard);
        });


        for (CardDTO card : playerModel.getTribe().getCharacters()) {
            addCharacterCard(card);
        }
        for (CardDTO card : playerModel.getTribe().getBuildings()) {
            addBuildingCard(card);
        }

    }

    /**
     * Adds a new building card to the dedicated building slot.
     *
     * @param card the building card DTO to add
     */
    private void addBuildingCard(CardDTO card) {

        Platform.runLater(() -> PlayerCardView.addNewCard(card.id, cardSlot7, cardImage7, cardCount7,  playerModel.getTribe().getBuildings().stream()
                .map(c -> c.id)
                .collect(Collectors.toList())
        ));
    }

    /**
     * Adds a new character card to the slot corresponding to its character type.
     * Uses the character type to select the correct slot from the parallel lists.
     *
     * @param card the character card DTO to add
     */
    private void addCharacterCard(CardDTO card) {
        if(card.characterType==null){
            return;
        }

        int selector = switch (card.characterType) {
            case ARTIST -> 0;
            case BUILDER -> 1;
            case GATHERER -> 2;
            case HUNTER -> 3;
            case INVENTOR -> 4;
            case SHAMAN -> 5;
        };

        characters.get(selector).add(card.id);

        Platform.runLater(()->
                PlayerCardView.addNewCard(card.id, stackPanes.get(selector), imagesView.get(selector), cardsCount.get(selector), characters.get(selector)));

    }

    /**
     * Rotates the header box by 180 degrees.
     * Used when this player board is displayed at the top of the screen
     * so the name and points are readable from the correct orientation.
     */
    public void setHeaderFlipped() {
        headerBox.setRotate(180);
    }

    /**
     * Updates the visibility and enabled state of the skip button.
     * The button is shown only for the main player during the RESOLVING_ACTIONS
     * state when an extra draw phase is active and when isMyTurn is true.
     *
     * @param currentState    the current game state
     * @param isExtraDrawPhase whether the game is currently in an extra draw phase
     */
    public void updateSkipButton(GameState currentState, boolean isExtraDrawPhase, boolean isMyTurn) {
        if (!isMainPlayer) return; // solo per il player principale

        boolean shouldShow = currentState == GameState.RESOLVING_ACTIONS && isExtraDrawPhase && isMyTurn;

        skipButton.setVisible(shouldShow);
        skipButton.setManaged(shouldShow);
        skipButton.setDisable(!shouldShow);
    }

    /**
     * Handles the skip button click.
     * Hides the button and sends the skip action to the server.
     */
    @FXML private void onSkipClicked() {
        skipButton.setVisible(false);
        skipButton.setManaged(false);
        skipButton.setDisable(true);

        clientController.skipOnExtraDraw(); // chiama l'azione sul server
    }

    /**
     * Applies the hover effect to the skip button when the mouse enters it.
     * Ignored if the button is currently disabled.
     */
    @FXML private void onSkipHover() {
        if (!skipButton.isDisable()) {
            UIEffects.applySkipHoverEffect(skipButton);
        }
    }

    /**
     * Restores the default style of the skip button when the mouse exits it.
     */
    @FXML private void onSkipHoverExit() {
        UIEffects.applySkipDefaultEffect(skipButton);
    }
}

