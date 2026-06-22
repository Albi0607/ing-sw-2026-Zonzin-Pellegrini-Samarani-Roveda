package it.polimi.ingsw.mesos.common.ClientModel;


import it.polimi.ingsw.mesos.common.enums.GameState;

import java.io.Serializable;
import java.util.List;

public class GameDTO implements Serializable {
    public List<PlayerDTO> players;
    public List<String> lastResolvedEvents;
    public BoardDTO board;
    public int currentRound;
    public String era;
    public GameState currentState;
    public String currentPlayerNickname;
    public boolean isUpper;
    public boolean isExtraDrawPhase;
    public List<PlayerDTO> winners;
}
