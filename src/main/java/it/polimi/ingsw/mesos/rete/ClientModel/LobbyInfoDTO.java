package it.polimi.ingsw.mesos.rete.ClientModel;

import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//classe per passare parametri delle partite a cui accedere ma senza dargli la possibilità di fare danni
public class LobbyInfoDTO implements Serializable {
    public int id;
    public int numPlayers;
    public int maxNumPlayers;
    public boolean started;
    public List<Color> takenColors = new ArrayList<>();

    //implementazione di equals per evitare aggiornamenti inutili in GUI (TotemChoiceController)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LobbyInfoDTO that = (LobbyInfoDTO) o;

        return id==that.id &&
                numPlayers==that.numPlayers &&
                maxNumPlayers==that.maxNumPlayers &&
                started==that.started &&
                Objects.equals(takenColors, that.takenColors);
    }
}
