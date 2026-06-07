package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.List;
import java.util.Map;

/**
 * Reconstructs the game state by replaying moves saved on disk.
 *
 * Workflow:
 *   1. Reads the list of GameMoves from the MoveLogger.
 *   2. Replays them one by one on the provided GameController.
 *   3. VirtualViews are replaced by dummies if a player has not yet reconnected with the same nickname.
 */
public class GameRestorer {

    private final MoveLogger logger;

    /**
     * Constructs a GameRestorer with the specified logger.
     *
     * @param logger the move logger to read from
     */
    public GameRestorer(MoveLogger logger) {
        this.logger = logger;
    }

    /**
     * Replays all saved moves on the provided controller.
     *
     * ADD_PLAYER moves require a VirtualView: they are retrieved from the views Map
     * as players reconnect.
     *
     * Gameplay moves (PLACE_TOTEM, TAKE_CARD, SKIP_EXTRA_DRAW) are executed normally.
     * Broadcast is disabled during replay to avoid flooding reconnected clients.
     * After the replay sends the current state to all reconnected clients
     *
     * @param controller the controller on which to replay the moves
     * @param views      a Map mapping nicknames to VirtualViews of reconnected clients
     * @return true if restoration was successful, false otherwise
     */
    public boolean restore(GameController controller, Map<String, VirtualView> views) {
        List<GameMove> moves = logger.readAll();

        if (moves.isEmpty()) {
            System.out.println("[GameRestorer] No moves saved, starting new game.");
            return false;
        }

        System.out.println("[GameRestorer] Restoring game with " + moves.size() + " moves...");

        // Disable broadcast during replay: clients will receive only the final state.
        controller.setReplayMode(true);

        try {
            for (GameMove move : moves) {
                System.out.println("[GameRestorer] Re-executing: " + move);
                replayMove(move, controller, views);
            }
        } catch (Exception e) {
            System.err.println("[GameRestorer] Error during restoration: " + e.getMessage());
            controller.setReplayMode(false);
            return false;
        }

        controller.setReplayMode(false);
        controller.sendClientStateToAll(ClientState.IN_GAME);
        controller.broadcastUpdate();

        System.out.println("[GameRestorer] Restoration complete.");
        return true;
    }

    /**
     * Executes a single move on the controller.
     *
     * ADD_PLAYER:
     * Use the reconnected client's VirtualView if the player is reconnected
     * Use a DummyVirtualView if the player is not reconnected yet.
     * The non reconnected player receives the update at the end.
     *
     * START_GAME:
     * Restore the entire decks before calling startGame
     * After startGame call the correct cards are drawn from restored decks
     * Restore playerOrder after the call since startGame shuffles it
     *
     * @param move       the move to replay
     * @param controller the controller on which to replay the move
     * @param views      Map of reconnected views to use for ADD_PLAYER moves
     */
    private void replayMove(GameMove move, GameController controller,
                            Map<String, VirtualView> views) {
        switch (move.type) {

            case SET_NUM_PLAYERS -> controller.setNumPlayers(move.intPayload);

            case ADD_PLAYER -> {
                VirtualView view = views.getOrDefault(
                        move.nickname,
                        new DummyVirtualView(move.nickname)
                );
                controller.addPlayer(move.nickname, move.colorPayload, view);
            }

            case START_GAME -> {
                StateSerializer ss = controller.getStateSerializer();

                if(ss.hasSavedState()) {
                    // 1. Restore the ENTIRE decks before calling startGame
                    ss.restoreDeck(controller.getGame().getBoard().getTribeDeck(), true);
                    ss.restoreDeck(controller.getGame().getBoard().getBuildingDeck(), false);
                }

                // 2. Calling startGame will now draw the correct cards from the restored deck
                controller.startGame();

                // 3. Fix player order (as startGame shuffles it)
                if(ss.hasSavedState()) {
                    List<String> order = ss.restorePlayerOrder();
                    if (order != null) {
                        controller.getGame().setPlayerOrder(order);
                    }
                }
            }

            case PLACE_TOTEM -> controller.onPlaceTotem(move.nickname, move.charPayload);

            case TAKE_CARD -> controller.onTakeCard(move.nickname, move.intPayload, move.boolPayload);

            case SKIP_EXTRA_DRAW -> controller.onSkipExtraDraw(move.nickname);
        }
    }

    /**
     * Returns the move logger.
     *
     * @return the MoveLogger instance
     */
    public MoveLogger getMoveLogger() {
        return logger;
    }
}