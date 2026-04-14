package it.polimi.ingsw.mesos.RMI.ClientModel;


import java.io.Serializable;
import java.util.List;

public class GameDTO implements Serializable {
    public List<PlayerDTO> players;
    public BoardDTO board;
    public int currentRound;
    public int era;
    public String currentState;
    public String currentPlayerId;
    public String message;
}
