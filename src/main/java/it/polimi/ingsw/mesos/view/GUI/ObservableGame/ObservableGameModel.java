package it.polimi.ingsw.mesos.view.GUI.ObservableGame;

import it.polimi.ingsw.mesos.common.ClientModel.*;
import it.polimi.ingsw.mesos.common.enums.GameState;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Observable model representing the full game state for the JavaFX GUI layer.
 *
 * Wraps a GameDTO into JavaFX observable properties and lists,
 * allowing UI components to bind directly to this model and react
 * automatically to any state change received from the server.
 *
 * Implements DTOUpdatable to support incremental updates from server-side DTOs.
 */
public class ObservableGameModel implements DTOUpdatable<GameDTO> {

    // Board lists: represent the current state of the game board
    private final ObservableList<CardDTO> upperRow = FXCollections.observableArrayList();
    private final ObservableList<CardDTO> lowerRow = FXCollections.observableArrayList();
    private final ObservableList<OfferTileDTO> offerTiles = FXCollections.observableArrayList();
    private final ObservableList<TurnOrderSlotDTO> turnOrderTrack = FXCollections.observableArrayList();

    // Player list
    private final ObservableList<ObservablePlayerModel> players = FXCollections.observableArrayList();

    // Observable property accessors: each method returns the JavaFX property
    // for UI binding
    private final ObservableList<String> lastResolvedEvents = FXCollections.observableArrayList();
    private final IntegerProperty currentRound = new SimpleIntegerProperty();
    private final StringProperty era = new SimpleStringProperty();
    private final StringProperty currentPlayerNickname = new SimpleStringProperty();
    private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>();
    private final BooleanProperty isUpper = new SimpleBooleanProperty();
    private final BooleanProperty extraDrawPhase = new SimpleBooleanProperty();

    public ObservableList<CardDTO> getUpperRow() {
        return upperRow;
    }
    public ObservableList<CardDTO> getLowerRow() {
        return lowerRow;
    }
    public ObservableList<OfferTileDTO> getOfferTiles() {
        return offerTiles;
    }
    public ObservableList<TurnOrderSlotDTO> getTurnOrderTrack() {
        return turnOrderTrack;
    }
    public ObservableList<ObservablePlayerModel> getPlayers() {
        return players;
    }
    public ObservableList<String> getLastResolvedEvents() {
        return lastResolvedEvents;
    }
    public IntegerProperty currentRoundProperty() {
        return currentRound;
    }
    public StringProperty eraProperty() {
        return era;
    }
    public StringProperty currentPlayerNicknameProperty() {
        return currentPlayerNickname;
    }
    public ObjectProperty<GameState> gameStateProperty() {
        return gameState;
    }
    public BooleanProperty isUpperProperty() {
        return isUpper;
    }
    public BooleanProperty extraDrawPhaseProperty() { return extraDrawPhase; }


    // Get methods to get the value
    public int getCurrentRound() {
        return currentRound.get();
    }
    public String getEra() {
        return era.get();
    }
    public String getCurrentPlayerNickname() {
        return currentPlayerNickname.get();
    }
    public GameState getGameState() {
        return gameState.get();
    }
    public boolean getIsUpper() {
        return isUpper.get();
    }
    public boolean isExtraDrawPhase() { return extraDrawPhase.get(); }

    /**
     * Updates this model from the provided GameDTO.
     *
     * Updates all observable properties and lists to reflect the latest
     * game state received from the server. If the DTO or its board is
     * null, the method returns without making any changes.
     *
     * @param game the DTO containing the updated game state
     */
    @Override
    public void updateFromDTO(GameDTO game) {

        if (game == null||game.board==null){
            return;
        }

        //Cambiamento delle variabili di gioco
        updateListIfChanged(lastResolvedEvents,game.lastResolvedEvents);
        currentRound.set(game.currentRound);
        era.set(game.era);
        currentPlayerNickname.set(game.currentPlayerNickname);
        gameState.set(game.currentState);
        isUpper.set(game.isUpper);
        extraDrawPhase.set(game.isExtraDrawPhase);


        BoardDTO board = game.board;

        //Creazione della board interamente
        updateListIfChanged(upperRow,board.upperRow);
        updateListIfChanged(lowerRow,board.lowerRow);
        updateListIfChanged(offerTiles,board.offerTiles);
        updateListIfChanged(turnOrderTrack,board.turnOrderSlots);


        //Aggiornamento di tutti i player
        updatePlayers(game.players);
    }

    /**
     * Updates the observable players list from a list of PlayerDTO.
     *
     * On the first call, creates a new ObservablePlayerModel for each player.
     * On subsequent calls, matches players by nickname and updates their data in place.
     *
     * @param playerDTOs the list of player DTOs to update from
     */
    private void updatePlayers(List<PlayerDTO> playerDTOs) {

        if (playerDTOs == null){
            return;
        }

        //al primo aggiornamento creo gli observable per tutti i player
        if(players.isEmpty()) {
            for (PlayerDTO dto : playerDTOs) {
                ObservablePlayerModel player = new ObservablePlayerModel();
                player.updateFromDTO(dto);
                players.add(player);
            }
            return;
        }
        //faccio update dei player per nome (anche se il gameController li restituisce in ordine)
        for (PlayerDTO dto : playerDTOs) {
            players.stream()
                    .filter(p -> p.getNickname().equals(dto.nickname))
                    .findFirst()
                    .ifPresent(p -> p.updateFromDTO(dto));
        }
    }

    /**
     * Updates an ObservableList only if its content differs from the new list,
     * avoiding unnecessary change events on the UI.
     * If newList is null or identical in size and content to
     * oldList, no update is performed.
     *
     * @param <T> the type of elements in the lists
     * @param oldList the observable list to update
     * @param newList the new list to compare against and copy from
     */
    public static <T> void updateListIfChanged(ObservableList<T> oldList, List<T> newList){
        if(newList==null){
            return;
        }
        if(oldList.size()==newList.size()&&oldList.equals(newList)){
            return;
        }
        oldList.setAll(newList);
    }

}