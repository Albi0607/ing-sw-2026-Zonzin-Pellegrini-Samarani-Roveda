package it.polimi.ingsw.mesos.RMI.ClientModel;

public enum ClientState {
    WAITING_CONNECTION,
    WAITING_PLAYERS,
    CHOOSE_PLAYERS,
    IN_GAME,
    END_GAME
}
