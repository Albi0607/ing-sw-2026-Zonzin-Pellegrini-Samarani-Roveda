package it.polimi.ingsw.mesos.network;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;

import java.util.List;

/**
 * Generic interface used by the server to communicate with clients without
 * knowing the underlying network implementation.
 *
 * All methods defined in this interface are invoked in the same way regardless
 * of whether the communication is performed through RMI or Socket.
 * Concrete implementations are responsible for translating these calls into
 * the appropriate network-specific operations.
 */
public interface VirtualView {

    /**
     * Sends the latest game state to the client so that it can be displayed
     * in the user interface.
     *
     * @param game the updated game state
     */
    void sendGame(GameDTO game);

    /**
     * Sends the current client state to the player.
     *
     * This information is mainly used before the game starts to determine
     * which actions are currently available.
     *
     * @param state the updated client state
     */
    void sendClientState(ClientState state);

    /**
     * Sends a generic notification or informational message to the client.
     *
     * @param message the message to display
     */
    void showMessage(String message);

    /**
     * Returns the nickname associated with the client.
     *
     * @return the client's nickname
     */
    String getNickname();


    /**
     * Sends the current lobby state to the client.
     *
     * The lobby information contains all available game sessions and their
     * current status, allowing the player to create or join a game.
     *
     * @param lobby the updated lobby information
     */
    void sendLobby(List<LobbyInfoDTO> lobby);

    /**
     * Returns the unique identifier associated with this client connection.
     *
     * The identifier is used internally by the server to track and manage
     * active client sessions.
     *
     * @return the unique connection identifier
     */
    String getId();

    /**
     * Notifies the client that a requested action has been rejected.
     *
     * @param reason the reason why the action could not be completed
     */
    void showActionRejected(String reason);

    /**
     * Notifies the client that a requested action has been successfully completed.
     *
     * @param message a confirmation message describing the successful operation
     */
    void showActionAccepted(String message);

    /**
     * Notifies the client that the login attempt has failed.
     *
     * @param message the reason for the login failure
     */
    void showLoginError(String message);

    /**
     * Sends the global leaderboard stored in the server database to the client.
     *
     * This method is available only when the server database is enabled.
     * The leaderboard contains the ranking of all players currently stored in the database,
     * including the players who have just completed the game.
     *
     * @param leaderboard the updated global leaderboard
     * @param myPosition the current player's position in the global ranking
     */
    void showLeaderboard(List<GameResult> leaderboard, int myPosition);

}
