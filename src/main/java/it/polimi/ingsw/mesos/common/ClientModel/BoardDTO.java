package it.polimi.ingsw.mesos.common.ClientModel;


import java.io.Serializable;
import java.util.List;

public class BoardDTO implements Serializable {
    public List<CardDTO> upperRow;
    public List<CardDTO> lowerRow;

    public List<OfferTileDTO> offerTiles;
    public List<TurnOrderSlotDTO> turnOrderSlots;

}