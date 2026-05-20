package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

import java.util.List;

public class LeaderboardMessage extends Message {

    private final List<GameResult> leaderboard;
    private final int myPosition;

    public LeaderboardMessage(List<GameResult> leaderboard, int myPosition) {
        this.leaderboard = leaderboard;
        this.myPosition = myPosition;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.showLeaderboard(leaderboard, myPosition);
    }
}