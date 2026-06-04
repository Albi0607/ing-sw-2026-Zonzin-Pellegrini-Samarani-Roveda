package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Message sent by the server to update the game state for the client.
 */
public class UpdateGameMessage extends Message {

    private final GameDTO game;

    /**
     * Constructs an UpdateGameMessage.
     *
     * @param game the game state DTO
     */
    public UpdateGameMessage(GameDTO game) {
        this.game = game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.updateGame(game);
    }
}
