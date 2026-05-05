package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;
/**
 * Message sent from the client to the server to select the number of players
 * for the game.
 * <p>
 * When received by the server, it updates the game configuration through
 * the {@link GameController}.
 * </p>
 */
public class ChoosePlayersMessage extends Message {
    private final int numPlayers;

    /**
     * Creates a new message containing the selected number of players.
     *
     * @param numPlayers the number of players chosen by the client
     */
    public ChoosePlayersMessage(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * Executes the message logic on the server side by setting the number of
     * players in the game controller.
     *
     * @param controller the game controller handling server-side game logic
     */
    @Override
    public void executeServerSide(GameController controller) {
        controller.setNumPlayers(numPlayers);
    }

    /**
     * Returns the number of players selected by the client.
     *
     * @return the number of players
     */
    public int getNumPlayers() {
        return numPlayers;
    }
}