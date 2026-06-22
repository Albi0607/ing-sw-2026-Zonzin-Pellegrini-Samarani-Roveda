package it.polimi.ingsw.mesos.common.ClientModel;

import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;
import java.util.Objects;

public class TurnOrderSlotDTO implements Serializable {

    public String occupantNickname;
    public Color occupantColor;
    public int modifier;

    //implementazione di equals per poter evitare aggiornamenti inutili in GUI
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TurnOrderSlotDTO that = (TurnOrderSlotDTO) o;
        return modifier==that.modifier&&Objects.equals(occupantNickname, that.occupantNickname)&& occupantColor==that.occupantColor;
    }
}
