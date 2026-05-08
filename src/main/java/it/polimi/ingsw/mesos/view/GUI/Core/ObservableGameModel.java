package it.polimi.ingsw.mesos.view.GUI.Core;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableGameModel {
    private final ObservableList<CardDTO> boardCards = FXCollections.observableArrayList();
    private final ObservableList<PlayerDTO> players = FXCollections.observableArrayList();
    private final ObservableList<OfferTileDTO> offerTiles = FXCollections.observableArrayList();
    private final ObservableList<TurnOrderSlotDTO> turnOrderTrack = FXCollections.observableArrayList();
    private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>();

    // Getter per le property (necessari per il binding) ...

    public void updateFromDTO(GameDTO game) {
        // Estrae e aggiorna SOLO i dati osservabili utili facendo uno smart merge
    }
}
