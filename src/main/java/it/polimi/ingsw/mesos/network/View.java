package it.polimi.ingsw.mesos.network;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;

import java.util.List;

/**
 * Generic interface for managing how the client views the game representation. This interface will be implemented by
 * both CLI and GUI and is used by the ClientController in a generic way, so it does not need to be concerned with which
 * type of view has been implemented
 */
public interface View {

    /**
     * Method that allows the client to display the game updated to reflect the latest changes from the model
     *
     * @param game latest game update
     */
    void showLastUpdate(GameDTO game);

    /**
     * Method that updates the attribute in the ClientController indicating the user's current state and, consequently,
     * the actions they must perform (mostly before the start of the game)
     *
     * @param state latest updated client state
     */
    void showClientStateUpdate(ClientState state);

    /**
     * Method that allows the client to display error or general messages related to the connection and game progress
     *
     * @param message message to be displayed in the client view
     */
    void showMessage(String message);

    /**
     * Displays the current lobby state to the user.
     * The lobby contains the list of available game sessions and their status, allowing the player to create or join a game.
     *
     * @param lobby the updated lobby information
     */
    void showLobby(List<LobbyInfoDTO> lobby);

    /**
     * Notifies the user that a requested action has been rejected.
     *
     * @param reason the reason why the action was not accepted
     */
    void showActionRejected(String reason);

    /**
     * Notifies the user that a requested action has been successfully completed.
     *
     * @param message confirmation message for the completed action
     */
    void showActionAccepted(String message);

    /**
     * Displays an error message related to a failed login attempt.
     *
     * @param message the reason for the login failure
     */
    void showLoginError(String message);

    /**
     * Displays the global leaderboard to the user.
     *
     * The leaderboard represents the ranking of all players stored in the system,
     * including players who have just completed a game.
     *
     * @param leaderboard the global ranking of players
     * @param myPosition the position of the current user in the ranking
     */
    void showLeaderboard(List<GameResult> leaderboard, int myPosition);

}
