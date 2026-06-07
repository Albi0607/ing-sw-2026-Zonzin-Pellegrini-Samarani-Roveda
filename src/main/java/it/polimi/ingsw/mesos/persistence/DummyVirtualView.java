package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;
import it.polimi.ingsw.mesos.view.CLI.UIEvent;

import java.util.List;

/**
 * A placeholder VirtualView used during replay for players who have not yet reconnected.
 * The player will receive the updated state once they reconnect and the controller
 * replaces this DummyVirtualView with their actual VirtualView.
 */
public class DummyVirtualView implements VirtualView {

    private final String nickname;

    /**
     * Constructs a DummyVirtualView for the specified player.
     *
     * @param nickname the player's nickname
     */
    public DummyVirtualView(String nickname) {
        this.nickname = nickname;
    }

    /**
     * {@inheritDoc}
     * This implementation discards the game state update.
     */
    @Override public void sendGame(GameDTO game)          { /* discard */ }

    /**
     * {@inheritDoc}
     * This implementation discards the state update.
     */
    @Override public void sendClientState(ClientState s)  { /* discard */ }

    /**
     * {@inheritDoc}
     * This implementation discards the message.
     */
    @Override public void showMessage(String message)     { /* discard */ }

    /**
     * {@inheritDoc}
     */
    @Override public String getNickname()                 { return nickname; }

    /**
     * {@inheritDoc}
     */
    @Override public void showActionRejected(String reason) {}

    /**
     * {@inheritDoc}
     */
    @Override public void showActionAccepted(String message) {}

    /**
     * {@inheritDoc}
     */
    @Override public void showLoginError(String message) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendLobby(List<LobbyInfoDTO> lobby) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return "";
    }

    /**
     * {@inheritDoc}
     * This implementation discards the leaderboard.
     */
    @Override
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) { /* discard */ }
}