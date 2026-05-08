package it.polimi.ingsw.mesos.rete.ClientModel;

import java.io.Serializable;

//classe per passare parametri delle partite a cui accedere ma senza dargli la possibilità di fare danni
public class LobbyInfoDTO implements Serializable {
    public int id;
    public int numPlayers;
    public int maxNumPlayers;
    public boolean started;
}
