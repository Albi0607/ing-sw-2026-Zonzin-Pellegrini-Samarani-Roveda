package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message_prova;

public class ChoosePlayersMessage extends Message_prova {
    private final int numPlayers;

    public ChoosePlayersMessage(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}