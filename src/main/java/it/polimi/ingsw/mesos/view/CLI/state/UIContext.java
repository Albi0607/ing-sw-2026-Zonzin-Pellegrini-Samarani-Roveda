package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;

import java.util.List;

/**
 * The Context interface for the State Pattern.
 * It provides the concrete UI States with a controlled set of methods to interact
 * with the main CLI engine, access data, send network commands, and trigger UI updates
 * without exposing the entire CLI implementation.
 */
public interface UIContext {

    /**
     * Transitions the CLI to a new active UIState, changing how subsequent inputs are handled.
     * @param newState The new state to transition to.
     */
    void transitionTo(UIState newState);

    /**
     * Retrieves the network controller used to send actions and requests to the server.
     * @return The ClientController instance.
     */
    ClientController getController();

    /**
     * Saves the local player's chosen nickname in the client's memory.
     * @param nickname The player's nickname.
     */
    void setMyNickname(String nickname);

    /**
     * Retrieves the local player's chosen nickname.
     * @return The player's nickname.
     */
    String getMyNickname();

    /**
     * Retrieves the most recent game state synchronized from the server.
     * @return The current GameDTO payload.
     */
    GameDTO getGameState();

    /**
     * Clears any pending user keyboard inputs from the event queue.
     * Crucial to prevent accidental "phantom" inputs from triggering actions immediately after a state transition.
     */
    void clearBufferedUserInputs();

    /**
     * Retrieves the currently active UIState managing the terminal.
     * @return The active UIState.
     */
    UIState getCurrentState();

    /**
     * Retrieves the latest list of available matches/lobbies fetched from the server.
     * @return A list of LobbyInfoDTOs.
     */
    List<LobbyInfoDTO> getLobby();

    /**
     * Retrieves the overarching client phase (e.g., LOBBY, WAITING_PLAYERS, IN_GAME, END_GAME).
     * @return The current ClientState.
     */
    ClientState getClientState();

    /**
     * Forces a direct re-render of the main user interface components (board, headers, etc.).
     */
    void drawUI();

    /**
     * Flushes the notification queue, printing all pending transient messages (e.g., errors, confirmations)
     * to the terminal at the correct moment in the rendering pipeline.
     */
    void flushNotifications();

    /**
     * Schedules a background timer (typically at the end of a match) to transition
     * to the final leaderboard screen after a brief delay.
     */
    void scheduleResolutionTimeout();

    /**
     * Locks or unlocks the UI's input capabilities while waiting for a network acknowledgment.
     * Automatically flags the UI for a soft redraw to display loading prompts.
     * @param value True to lock the UI and wait for the server, false to unlock.
     */
    void setAwaitingServerResponse(boolean value);

    /**
     * Checks if the rendering engine is currently scheduled to perform a full screen clear and redraw.
     * @return True if a full redraw is pending.
     */
    boolean isfullDirty();
}
