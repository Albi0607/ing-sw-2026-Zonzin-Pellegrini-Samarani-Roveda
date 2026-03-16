package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;

public interface GameStateLogic {

    /**
     * Execute the logic for each state.
     * Each state is responsible for calling game.changeState() when done.
     */
    void execute(Game game);
}
