package it.polimi.ingsw.mesos.RMI.ClientModel;


import it.polimi.ingsw.mesos.model.enums.GameState;

import java.io.Serializable;
import java.util.List;

public class GameDTO implements Serializable {
    public List<PlayerDTO> players;
    public BoardDTO board;
    public int currentRound;
    public int era;
    public GameState currentState;
    public String currentPlayerNickname;
    public boolean isUpper;
    public String message;
}
