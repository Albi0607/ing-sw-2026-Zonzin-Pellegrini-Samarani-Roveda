package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;

public interface View {

    //per mostrare la view sempre aggiornata
    void showLastUpdate(GameDTO game);

    void showClientStateUpdate(ClientState state);

    void showMessage(String message);
}
