package it.polimi.ingsw.mesos.common.ClientModel;

import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;
import java.util.Objects;

public class OfferTileDTO implements Serializable {

    public String id;
    public String occupantNickname;
    public Color occupantColor;

    //implementazione di equals per poter evitare aggiornamenti inutili in GUI
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfferTileDTO that = (OfferTileDTO) o;
        return Objects.equals(id, that.id)&&Objects.equals(occupantNickname, that.occupantNickname)&&occupantColor==that.occupantColor;
    }
}
