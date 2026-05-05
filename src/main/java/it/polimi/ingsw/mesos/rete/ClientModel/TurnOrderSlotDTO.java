package it.polimi.ingsw.mesos.rete.ClientModel;

import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;

public class TurnOrderSlotDTO implements Serializable {

    public String occupantNickname;
    public Color occupantColor;
    public int modifier;
}
