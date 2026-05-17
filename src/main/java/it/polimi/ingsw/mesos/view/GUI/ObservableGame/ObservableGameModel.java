package it.polimi.ingsw.mesos.view.GUI.ObservableGame;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ObservableGameModel {

    //Attributi della board che si aggiornano automaticamente a ogni cambiamento
    private final ObservableList<CardDTO> upperRow = FXCollections.observableArrayList();
    private final ObservableList<CardDTO> lowerRow = FXCollections.observableArrayList();
    private final ObservableList<OfferTileDTO> offerTiles = FXCollections.observableArrayList();
    private final ObservableList<TurnOrderSlotDTO> turnOrderTrack = FXCollections.observableArrayList();

    //Attributo che serve per gestire tutti i player in gioco
    private final ObservableList<ObservablePlayerModel> players = FXCollections.observableArrayList();

    //Attributi generali di gioco che si aggiornano automaticamente
    private final ObservableList<String> lastResolvedEvents = FXCollections.observableArrayList();
    private final IntegerProperty currentRound = new SimpleIntegerProperty();
    private final StringProperty era = new SimpleStringProperty();
    private final StringProperty currentPlayerNickname = new SimpleStringProperty();
    private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>();
    private final BooleanProperty isUpper = new SimpleBooleanProperty();


    //metodi get per ottenere gli attributi
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

    //metodi get per ottenere direttamente il valore
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

    //Metodo update che aggiorna il modello osservabile a ogni cambiamento e a ogni chiamata di update da parte del server
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


        BoardDTO board = game.board;

        //Creazione della board interamente
        updateListIfChanged(upperRow,board.upperRow);
        updateListIfChanged(lowerRow,board.lowerRow);
        updateListIfChanged(offerTiles,board.offerTiles);
        updateListIfChanged(turnOrderTrack,board.turnOrderSlots);


        //Aggiornamento di tutti i player
        updatePlayers(game.players);
    }

    //Metodo update di tutti i parametri di tutti i player in gioco
    // TODO Aggiornare in modo che modifichi solo le parti che cambiano e le restanti rimangono invariate
    private void updatePlayers(List<PlayerDTO> playerDTOs) {

        if (playerDTOs == null) return;

        players.clear();

        for (PlayerDTO dto : playerDTOs) {

            ObservablePlayerModel player = new ObservablePlayerModel();
            player.updateFromDTO(dto);

            players.add(player);
        }
    }


    public <T> void updateListIfChanged(ObservableList<T> oldList, List<T> newList){
        if(newList==null){
            return;
        }
        if(oldList.size()==newList.size()&&oldList.equals(newList)){
            return;
        }
        oldList.setAll(newList);
    }

}