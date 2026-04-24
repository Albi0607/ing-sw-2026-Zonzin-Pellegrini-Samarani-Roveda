package it.polimi.ingsw.mesos.rete.ClientModel;


import java.io.Serializable;
import java.util.List;

public class BoardDTO implements Serializable {
    public List<CardDTO> upperRow;
    public List<CardDTO> lowerRow;

    public int tribeDeckSize;
    public int buildingDeckSize;

    public List<OfferTileDTO> offerTiles;
    public List<TurnOrderSlotDTO> turnOrderSlots;

    //potrebbe dovermi servire anche qualcosa che definisce il turnOrderTrack e l'offerTile anche se forse questo
    //potrebbe gestirlo direttamente la view perchè dipende solo dal numero di giocatori che partecipano alla partita
}