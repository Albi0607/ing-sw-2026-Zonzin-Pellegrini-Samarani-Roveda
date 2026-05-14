package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;
import it.polimi.ingsw.mesos.view.CLI.UIEvent;

import java.util.List;

/**
 * VirtualView fittizia usata durante il replay per i giocatori
 * che non si sono ancora riconnessi.
 * il giocatore riceverà lo stato aggiornato quando si riconnette e il controller
 * sostituisce questa DummyVirtualView con la sua vera VirtualView.
 */
public class DummyVirtualView implements VirtualView {

    private final String nickname;

    public DummyVirtualView(String nickname) {
        this.nickname = nickname;
    }

    @Override public void sendGame(GameDTO game)          { /* scarta */ }
    @Override public void sendClientState(ClientState s)  { /* scarta */ }
    @Override public void showMessage(String message)     { /* scarta */ }
    @Override public String getNickname()                 { return nickname; }
    @Override public void showActionRejected(String reason) {}
    @Override public void showActionAccepted(String message) {}
    @Override public void showLoginError(String message) {}


    @Override
    public void sendLobby(List<LobbyInfoDTO> lobby) {
    }

    @Override
    public String getId() {
        return "";
    }
}