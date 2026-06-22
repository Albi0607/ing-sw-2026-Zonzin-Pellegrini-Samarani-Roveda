package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.network.VirtualView;
import it.polimi.ingsw.mesos.socket.Message.messageClient.LeaderboardMessage;
import it.polimi.ingsw.mesos.socket.Message.messageClient.PingMessage;
import it.polimi.ingsw.mesos.socket.Message.messageServer.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Implementation of the {@link VirtualView} interface for the Socket-based communication protocol.
 *
 * This class acts as a bridge between the server-side GameController and a remote client.
 * It translates high-level game events and controller calls into serialized message objects
 * that are sent over a TCP socket using an {@link ObjectOutputStream}.
 *
 * Instances of this class are typically created by a {@link ClientHandler} once a player
 * has successfully registered with a unique nickname and the communication streams are established.
 */
public class SocketVirtualView implements VirtualView {

    private final String nickname;
    private final ObjectOutputStream out;

    private final String id = java.util.UUID.randomUUID().toString();

    /**
     * Constructs a new SocketVirtualView for a specific player.
     *
     * @param nickname the player's nickname
     * @param out      the output stream to the client
     */
    public SocketVirtualView(String nickname, ObjectOutputStream out) {
        this.nickname = nickname;
        this.out      = out;
    }

    /**
     * Sends the updated game state to the client.
     * Called by GameController.broadcastUpdate() after each action.
     *
     * @param game the game state DTO
     */
    @Override
    public synchronized void sendGame(GameDTO game) {
        send(new UpdateGameMessage(game));
    }

    /**
     * Notifies the client of a change in their state.
     *
     * @param state the new client state
     */
    @Override
    public synchronized void sendClientState(ClientState state) {
        send(new ClientStateMessage(state));
    }

    /**
     * Sends a text message to the client.
     * This is not broadcasted; it is sent only to this specific client.
     *
     * @param message the message content
     */
    @Override
    public synchronized void showMessage(String message) {
        send(new ErrorMessage(message));
    }

    /**
     * Notifies the client that their action was rejected.
     *
     * @param reason the reason for rejection
     */
    @Override
    public synchronized void showActionRejected(String reason) {
        send(new ActionRejectedMessage(reason));
    }

    /**
     * Notifies the client that their action was accepted.
     *
     * @param message a confirmation message
     */
    @Override
    public synchronized void showActionAccepted(String message) {
        send(new ActionAcceptedMessage(message));
    }

    /**
     * Notifies the client of an error during the login process.
     *
     * @param message the error message
     */
    @Override
    public synchronized void showLoginError(String message) { send(new LoginErrorMessage(message)); }

    /**
     * Writes an object to the output stream.
     * Uses reset() to prevent the ObjectOutputStream from caching objects,
     * ensuring that subsequent updates to the same object are sent correctly.
     *
     * @param message the object to send
     */
    private synchronized void send(Object message) {
        try {
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println("Error sending message to " + nickname + ": " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Sends the current lobby information to the client.
     *
     * @param lobby the list of lobby info DTOs
     */
    @Override
    public synchronized void sendLobby(List<LobbyInfoDTO> lobby) {
        send(new LobbyUpdateMessage(lobby));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sends a ping message to the client to keep the connection alive.
     */
    public synchronized void sendPing() {
        send(new PingMessage());
    }

    /**
     * Sends the final leaderboard and the player's position to the client.
     *
     * @param leaderboard the list of game results
     * @param myPosition  the player's rank
     */
    @Override
    public synchronized void showLeaderboard(List<GameResult> leaderboard, int myPosition) {
        send(new LeaderboardMessage(leaderboard, myPosition));
    }

}