package it.polimi.ingsw.mesos.rete.ClientModel;

//classe per passare parametri delle partite a cui accedere ma senza dargli la possibilità di fare danni
public class LobbyInfoDTO {
    public int id;
    public int numPlayers;
    public int maxNumPlayers;
    public boolean started;
}
