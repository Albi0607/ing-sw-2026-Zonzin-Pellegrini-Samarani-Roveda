package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;

public interface VirtualView {

    void sendGame(GameDTO game);

    void sendClientState(ClientState state);
    //capire cosa farci soprattutto in RMI
    void showMessage(String message);

    String getNickname();
}
