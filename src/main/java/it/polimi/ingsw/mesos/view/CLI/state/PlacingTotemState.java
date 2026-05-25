package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

/**
 * Represents the active UI state when it is the local player's turn
 * to place their totem on an Offer Tile.
 */
public class PlacingTotemState implements UIState {

    /**
     * Processes the user's input to select an Offer Tile.
     * Validates that the input is a single character, sends the placement command
     * to the server, and immediately locks the UI by transitioning to the Waiting state
     * to prevent spamming inputs while the server processes the move.
     *
     * @param input   The raw string typed by the user (expected to be a single letter like 'A' or 'B').
     * @param context The UIContext providing access to the network controller and state transitions.
     */
    @Override
    public void handleInput(String input, UIContext context) {

        if (context.getGameState().currentState != GameState.PLACING_TOTEMS) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: La fase dei totem è terminata." + CLIPrinter.ANSI_RESET);
            context.transitionTo(WaitingState.INSTANCE);
            return;
        }

        String choice = input.toUpperCase();
        if (choice.isEmpty() || choice.length() != 1) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Lettera non valida!" + CLIPrinter.ANSI_RESET);
            renderPrompt(context);
        } else {
            context.getController().placeTotem(choice.charAt(0));

            context.clearBufferedUserInputs();
            context.setAwaitingServerResponse(true);
            context.transitionTo(WaitingState.INSTANCE);
        }
    }

    /**
     * Prints the lightweight prompt guiding the user to type a valid Offer Tile letter.
     */
    @Override
    public void renderPrompt(UIContext context) {
        System.out.print("Scegli la tessera per il totem (A, B, C, D, E, F, G): ");
    }

    /**
     * Delegates the heavy rendering of the game board to the central context.
     */
    @Override
    public void render(UIContext context) {
        context.drawUI();
    }
}
