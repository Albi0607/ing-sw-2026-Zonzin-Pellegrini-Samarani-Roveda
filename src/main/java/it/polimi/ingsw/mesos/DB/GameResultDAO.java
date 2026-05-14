package it.polimi.ingsw.mesos.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameResultDAO {

    public GameResultDAO() {
        // costruttore vuoto
    }

    private Connection getConnection() throws SQLException {
        return DBManager.getConnection();
    }

    public void save(GameResult result) throws SQLException {
        String sql = "INSERT INTO game_results (nickname, points, num_players, game_date) VALUES (?, ?, ?, NOW())";
        Connection conn = getConnection(); // ← non nel try-with-resources
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, result.getNickname());
            ps.setInt(2, result.getPoints());
            ps.setInt(3, result.getNumPlayers());
            ps.executeUpdate();
        }
    }

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

    public void clearAll() throws SQLException {
        String sql = "DELETE FROM game_results";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}

