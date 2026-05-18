package it.polimi.ingsw.mesos.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for persisting and retrieving {@link GameResult} instances.
 *
 * <p>Provides CRUD operations against the {@code game_results} table in the database
 * managed by {@link DBManager}. The connection is obtained from {@link DBManager#getConnection()}
 * and is intentionally <em>not</em> closed by this class, since its lifecycle is
 * managed externally by {@code DBManager}.
 *
 * <p>All methods that interact with the database declare {@link SQLException} and
 * expect the caller to handle or propagate it. Before invoking any method, callers
 * should verify that the database is available via {@link DBManager#isActive()}.
 */

public class GameResultDAO {

    public GameResultDAO() {
    }

    /**
     * Returns the active database connection from {@link DBManager}.
     *
     * <p>The returned connection is shared and managed by {@link DBManager};
     *
     * @return the current {@link Connection}
     * @throws SQLException if the connection is {@code null} or unavailable
     */
    private Connection getConnection() throws SQLException {
        Connection conn = DBManager.getConnection();
        if (conn == null) throw new SQLException("Database connection is not available.");
        return conn;
    }

    /**
     * Persists a {@link GameResult} to the {@code game_results} table.
     *
     * <p>Inserts a new row with the player's nickname, score, and number of players.
     * The {@code game_date} column is set to the current database server time via
     * {@code NOW()}
     *
     * <p>The underlying connection is obtained from {@link DBManager} and is
     * not closed after this operation.
     *
     * @param result the {@link GameResult} to save; must not be {@code null},
     *               and its {@code nickname} must not be {@code null}
     * @throws SQLException if a database access error occurs or the connection
     *                      is unavailable
     */
    public void save(GameResult result) throws SQLException {
        String sql = "INSERT INTO game_results (nickname, points, num_players, game_date) VALUES (?, ?, ?, NOW())";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, result.getNickname());
            ps.setInt(2, result.getPoints());
            ps.setInt(3, result.getNumPlayers());
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves the leaderboard for games played with a specific number of players,
     * ordered by score in descending order.
     *
     * <p>Each row in the result set is mapped to a {@link GameResult} instance,
     * including its {@code game_date} timestamp. The list is ordered from highest
     * to lowest score.
     *
     * @param numPlayers the number of players used to filter the leaderboard entries
     * @return a {@link List} of {@link GameResult} objects ordered by points descending;
     *         never {@code null}, but may be empty if no matching records exist
     * @throws SQLException if a database access error occurs or the connection
     *                      is unavailable
     */
    public List<GameResult> getLeaderboard(int numPlayers) throws SQLException {
        List<GameResult> list = new ArrayList<>();
        String sql = """
        SELECT nickname, points, num_players, game_date
        FROM game_results
        WHERE num_players = ?
        ORDER BY points DESC
    """;
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numPlayers);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GameResult gr = new GameResult(
                        rs.getString("nickname"),
                        rs.getInt("points"),
                        rs.getInt("num_players")
                );
                gr.setGameDate(rs.getTimestamp("game_date"));
                list.add(gr);
            }
        }
        return list;
    }

    /**
     * Returns the 1-based leaderboard position of a player for games with a given
     * number of participants.
     *
     * <p>Fetches all records for the specified {@code numPlayers} value, ordered by
     * score descending, and iterates through them to find the first entry whose
     * nickname matches the given value. If multiple records exist for the same
     * nickname, only the highest-scoring one determines the position.
     *
     * @param nickname   the player's nickname to look up; must not be {@code null}
     * @param numPlayers the number of players used to filter the leaderboard
     * @return the 1-based position of the player in the leaderboard,
     *         or {@code -1} if no matching record is found
     * @throws SQLException if a database access error occurs or the connection
     *                      is unavailable
     */
    public int getPlayerPosition(String nickname, int numPlayers) throws SQLException {
        String sql = """
        SELECT nickname
        FROM game_results
        WHERE num_players = ?
        ORDER BY points DESC
    """;
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numPlayers);
            ResultSet rs = ps.executeQuery();
            int position = 1;
            while (rs.next()) {
                if (rs.getString("nickname").equals(nickname)) return position;
                position++;
            }
        }
        return -1;
    }

    /**
     * Deletes all records from the {@code game_results} table.
     *
     * <p>This operation is irreversible. It affects all rows regardless of
     * nickname, score, or player count. Intended for administrative use or
     * test cleanup only.
     *
     * @throws SQLException if a database access error occurs or the connection
     *                      is unavailable
     */
    public void clearAll() throws SQLException {
        String sql = "DELETE FROM game_results";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}

