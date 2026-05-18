package it.polimi.ingsw.mesos.DB;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Represents the result of a completed game session for a single player.
 *
 * <p>Stores the player's nickname, their final score, the number of participants
 * in the game, and an optional timestamp indicating when the game took place.
 * Instances are typically created at the end of a game and persisted to the
 * database via a repository or DAO layer.
 *
 * <p>The {@code gameDate} field is not set at construction time; it is populated
 * after retrieval from the database using {@link #setGameDate(Timestamp)}.
 *
 */

public class GameResult {
    private String nickname;
    private int points;
    private int numPlayers;
    private Timestamp gameDate;

    public GameResult(String nickname, int points, int numPlayers) {
        this.nickname = nickname;
        this.points = points;
        this.numPlayers = numPlayers;
    }

    public String getNickname() { return nickname; }
    public int getPoints() { return points; }
    public int getNumPlayers() { return numPlayers; }
    public Timestamp getDate() {
        return gameDate;
    }

    public void setGameDate(Timestamp gameDate) {
        this.gameDate = gameDate;
    }
}


