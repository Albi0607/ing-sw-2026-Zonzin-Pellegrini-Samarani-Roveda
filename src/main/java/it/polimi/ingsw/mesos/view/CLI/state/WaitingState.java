package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.PlayerDTO;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

/**
 * Represents a passive UI state where the local client is waiting.
 * Used when it's an opponent's turn or when waiting for a server acknowledgment.
 * Implemented as a Singleton to save memory since it holds no internal mutable state.
 */
public class WaitingState implements UIState {

    /** The single, globally accessible instance of this passive state. */
    public static final WaitingState INSTANCE = new WaitingState();

    /** Private constructor to enforce the Singleton pattern. */
    private WaitingState() {}

    /**
     * Intentionally left blank.
     * Acts as an input "sink" or "lock", consuming and discarding any keyboard
     * input from the user while they are supposed to be waiting.
     *
     * @param input   The raw string typed by the user (discarded).
     * @param context The UIContext.
     */
    @Override
    public void handleInput(String input, UIContext context) {
    }

    /**
     * Renders the main heavy visual elements if the user is in an active match.
     * Ensures the board remains visible while waiting for opponents.
     */
    @Override
    public void render(UIContext context) {

        if (context.getClientState() == it.polimi.ingsw.mesos.rete.ClientModel.ClientState.IN_GAME) {
            context.drawUI();
        }
    }

    /**
     * Prints a contextual, lightweight message at the bottom of the screen,
     * informing the local user about who is currently playing and what phase they are in.
     */
    @Override
    public void renderPrompt(UIContext context){

        if (!context.isfullDirty()) {
            return;
        }

        if (context.getGameState() == null) return;

        String currentPlayer = context.getGameState().currentPlayerNickname;
        GameState phase = context.getGameState().currentState;

        switch (phase) {

            case PLACING_TOTEMS ->
                    System.out.println(
                            "\n⏳ In attesa che "
                                    + currentPlayer.toUpperCase()
                                    + " piazzi il totem..."
                    );

            case RESOLVING_ACTIONS ->
                    System.out.println(
                            "\n⏳ In attesa che "
                                    + currentPlayer.toUpperCase()
                                    + " scelga una carta..."
                    );

            default ->
                    System.out.println(
                            "\n⏳ In attesa della prossima mossa..."
                    );
        }
    }
}