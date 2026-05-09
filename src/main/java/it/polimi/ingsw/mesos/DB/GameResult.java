package it.polimi.ingsw.mesos.DB;

import java.sql.Timestamp;
import java.time.LocalDate;

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
    public Timestamp getGameDate() { return gameDate; }

    public void setGameDate(Timestamp gameDate) {
        this.gameDate = gameDate;
    }

    public Timestamp getDate() {
        return gameDate;
    }
}


