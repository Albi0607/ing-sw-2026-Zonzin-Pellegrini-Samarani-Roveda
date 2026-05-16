package it.polimi.ingsw.mesos.rete.ClientModel;

import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//classe per passare parametri delle partite a cui accedere ma senza dargli la possibilità di fare danni
public class LobbyInfoDTO implements Serializable {
    public int id;
    public int numPlayers;
    public int maxNumPlayers;
    public boolean started;
    public List<Color> takenColors = new ArrayList<>();
}
