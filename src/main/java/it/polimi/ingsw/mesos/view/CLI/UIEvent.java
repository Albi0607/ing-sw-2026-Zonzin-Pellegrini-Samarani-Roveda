package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;

import java.util.List;

/**
 * Base marker interface for all asynchronous events handled by the CLI's main Event Loop.
 * Every record inside represents a specific "message" that can be processed by the UIStates.
 */
public interface UIEvent {

    /** * Carries the latest, fully updated game state payload received from the server.
     */
    record GameUpdatedEvent(GameDTO game) implements UIEvent {}

    /** * Contains the updated list of available matches/lobbies to display in the waiting room.
     */
    record LobbyUpdatedEvent(List<LobbyInfoDTO> lobby) implements UIEvent {}

    /** * Signals a macro-phase transition for the client (e.g., moving from LOBBY to IN_GAME).
     */
    record ClientStateUpdatedEvent(ClientState state) implements UIEvent {}

    /** * Represents a generic informational notification to be displayed to the user (e.g., "Player joined").
     */
    record MessageEvent(String message) implements UIEvent {}

    /** * Encapsulates a raw string of text inputted by the user via the terminal.
     */
    record UserInputEvent(String input) implements UIEvent {}

    /** * Indicates that the server denied the last requested action, containing the error reason.
     */
    record ActionRejectedEvent(String reason) implements UIEvent {}

    /** * Confirms that the server successfully validated and processed the last requested action.
     */
    record ActionAcceptedEvent(String message) implements UIEvent {}

    /** * Signifies a failed login attempt (e.g., nickname already in use or invalid).
     */
    record LoginErrorEvent(String message) implements UIEvent {}

    /** * A local background timer event used to transition the screen from the "Game Over" board to the Leaderboard.
     */
    record ResolutionTimeoutEvent() implements UIEvent {}

    /** * Triggered when a crashed or disconnected game is successfully recovered and synchronized from server logs.
     */
    record GameRestoredEvent(GameDTO game) implements UIEvent {}

    /** * Carries the global database leaderboard statistics and the local player's specific ranking index.
     */
    record LeaderboardReadyEvent(List<GameResult> leaderboard, int myPosition) implements UIEvent {}

}
