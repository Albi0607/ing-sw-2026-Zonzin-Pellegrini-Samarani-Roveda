package it.polimi.ingsw.mesos.DB;

import java.sql.SQLException;
import java.util.List;

public class LeaderboardService {

    private final GameResultDAO dao;

    public LeaderboardService(GameResultDAO dao) {
        this.dao = dao;
    }

    public void addResult(String nickname, int points, int numPlayers) throws SQLException {
        GameResult result = new GameResult(nickname, points, numPlayers);
        dao.save(result);
    }

    public int getPosition(String nickname, int numPlayers) throws SQLException {
        return dao.getPlayerPosition(nickname, numPlayers);
    }

    public List<GameResult> getLeaderboard(int numPlayers) throws SQLException {
        return dao.getLeaderboard(numPlayers);
    }
}