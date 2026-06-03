package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

import java.util.List;

/**
 * Message sent to the client at the end of a game to display the leaderboard.
 *
 * <p>Carries the final rankings and the receiving player's position,
 * then delegates rendering to {@link ClientController#showLeaderboard(List, int)}.
 */
public class LeaderboardMessage extends Message {

    private final List<GameResult> leaderboard;
    private final int myPosition;

    /**
     * Constructs a {@code LeaderboardMessage} with the given rankings and player position.
     *
     * @param leaderboard an ordered list of {@link GameResult} entries representing the final rankings
     * @param myPosition  the zero-based position of the receiving player in the leaderboard
     */
    public LeaderboardMessage(List<GameResult> leaderboard, int myPosition) {
        this.leaderboard = leaderboard;
        this.myPosition = myPosition;
    }

    /**
     * Executes this message on the client side by invoking
     * {@link ClientController#showLeaderboard(List, int)}.
     *
     * @param controller the {@link ClientController} that will display the leaderboard
     */
    @Override
    public void executeClientSide(ClientController controller) {
        controller.showLeaderboard(leaderboard, myPosition);
    }
}