package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;

public class ChoosePlayersMessage extends Message {
    private final int numPlayers;

    public ChoosePlayersMessage(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    @Override
    public void executeServerSide(GameController controller) {
        controller.setNumPlayers(numPlayers);
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}