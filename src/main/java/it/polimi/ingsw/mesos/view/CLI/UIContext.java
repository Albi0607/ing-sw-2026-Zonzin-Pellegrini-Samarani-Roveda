package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.CLI.state.UIState;

import java.util.List;

public interface UIContext {
    void transitionTo(UIState newState);

    ClientController getController();

    void setMyNickname(String nickname);

    String getMyNickname();

    GameDTO getGameState();

    void clearBufferedUserInputs();

    UIState getCurrentState();

    List<LobbyInfoDTO> getLobby();
    ClientState getClientState();
    void drawUI();
    void flushNotifications(); // Per stampare le notifiche al momento giusto
    void scheduleResolutionTimeout();
}
