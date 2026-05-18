package it.polimi.ingsw.mesos.DB;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for leaderboard operations.
 *
 * <p>Acts as a facade over {@link GameResultDAO}, providing a higher-level API
 * for saving game results and querying the leaderboard. Controllers and game logic
 * should interact with this class rather than with the DAO directly.
 *
 * <p>All methods propagate {@link SQLException} to the caller; it is the caller's
 * responsibility to handle database unavailability, for example by checking
 * {@link DBManager#isActive()} before invoking any method.
 */

public class LeaderboardService {

    private final GameResultDAO dao;

    public LeaderboardService(GameResultDAO dao) {
        this.dao = dao;
    }

    /**
     * Records the result of a completed game for a single player.
     *
     * <p>Creates a new {@link GameResult} from the provided data and persists
     * it to the database. The game timestamp is set server-side at insertion time.
     *
     * @param nickname   the player's in-game nickname; must not be {@code null}
     * @param points     the player's final score
     * @param numPlayers the total number of players in the game session
     * @throws SQLException if a database access error occurs or the connection
     *                      is unavailable
     */
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