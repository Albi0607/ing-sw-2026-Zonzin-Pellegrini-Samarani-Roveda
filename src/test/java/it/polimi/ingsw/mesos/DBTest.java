package it.polimi.ingsw.mesos;

import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

public class DBTest {

    private static LeaderboardService service;
    private static GameResultDAO dao;

    @BeforeAll
    static void setup() throws Exception {
        String username = "root";
        String pw = "1234";

        DBManager.init(username, pw);

        dao = new GameResultDAO();
        service = new LeaderboardService(dao);

        dao.clearAll();
    }

    @Test
    void testInsertAndLeaderboard() throws SQLException {
        service.addResult("Mario", 100, 3);
        service.addResult("Luigi", 80, 3);
        service.addResult("Peach", 120, 3);

        List<?> leaderboard = service.getLeaderboard(3);

        assertEquals(3, leaderboard.size());

        // Peach deve essere primo
        assertEquals("Peach", service.getLeaderboard(3).get(0).getNickname());
    }

    @Test
    void testPositions() throws SQLException {
        service.addResult("Mario", 100, 3);
        service.addResult("Luigi", 80, 3);
        service.addResult("Peach", 120, 3);

        int posMario = service.getPosition("Mario", 3);
        int posPeach = service.getPosition("Peach", 3);
        int posLuigi = service.getPosition("Luigi", 3);

        assertTrue(posPeach < posMario);
        assertTrue(posMario < posLuigi);
    }
}