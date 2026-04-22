package it.polimi.ingsw.mesos.rete.ClientModel;

import it.polimi.ingsw.mesos.model.enums.Color;

import java.io.Serializable;

public class OfferTileDTO implements Serializable {

    public String id;
    public String occupantNickname;
    public Color occupantColor;
}
