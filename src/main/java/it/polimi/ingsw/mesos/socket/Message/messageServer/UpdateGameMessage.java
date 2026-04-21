package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

public class UpdateGameMessage extends Message {

    private final GameDTO game;

    public UpdateGameMessage(GameDTO game) {
        this.game = game;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.updateGame(game);
    }
}
