package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.enums.GameState;

public interface GameStateLogic {

    void execute(Game game);
    GameState getStateId();
}
