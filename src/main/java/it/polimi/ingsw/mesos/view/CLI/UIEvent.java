package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;

import java.util.List;

// 1. La nostra interfaccia pulita (senza metodi, serve solo come "etichetta")
public interface UIEvent {

    // 2. Tutti i nostri eventi raggruppati qui dentro!
    record GameUpdatedEvent(GameDTO game) implements UIEvent {}

    record LobbyUpdatedEvent(List<LobbyInfoDTO> lobby) implements UIEvent {}

    record ClientStateUpdatedEvent(ClientState state) implements UIEvent {}

    record MessageEvent(String message) implements UIEvent {}

    record UserInputEvent(String input) implements UIEvent {}

    record ActionRejectedEvent(String reason) implements UIEvent {}

    record ActionAcceptedEvent(String message) implements UIEvent {}
    record ResolutionTimeoutEvent() implements UIEvent {}

}
