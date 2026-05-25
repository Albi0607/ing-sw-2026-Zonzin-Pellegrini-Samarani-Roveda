package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

/**
 * Represents the UI state where the active player must select a specific card
 * from the board during the resolution phase.
 * It handles numeric input, converting the 1-based user choice into a 0-based
 * index for the server.
 */
public class ChoosingCardIdState implements UIState {

    /**
     * Processes the user's numeric input to pick a card.
     * Validates that the input is a valid number, maps it to the correct row (Upper/Lower),
     * dispatches the network command, and safely locks the UI.
     *
     * @param input   The raw string typed by the user (expected to be a number).
     * @param context The UIContext providing access to the network controller and state transitions.
     */
    @Override
    public void handleInput(String input, UIContext context) {

        if (context.getGameState().currentState != GameState.RESOLVING_ACTIONS) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: La fase delle azioni è terminata." + CLIPrinter.ANSI_RESET);
            context.transitionTo(WaitingState.INSTANCE);
            return;
        }

        try {
            int cardIndex = Integer.parseInt(input) - 1;
            boolean isUpper = context.getGameState().isUpper;

            context.getController().takeCard(cardIndex, isUpper);

            context.clearBufferedUserInputs();
            context.setAwaitingServerResponse(true);
            context.transitionTo(WaitingState.INSTANCE);

        } catch (NumberFormatException e) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Numero non valido!" + CLIPrinter.ANSI_RESET);
            renderPrompt(context);
        }
    }

    /**
     * Prints the lightweight prompt guiding the user.
     * Dynamically informs the player whether they are picking from the UPPER or LOWER row based on the game rules.
     */
    @Override
    public void renderPrompt(UIContext context) {
        boolean isUpper = context.getGameState().isUpper;
        String nomeFila = isUpper ? "SUPERIORE (↑)" : "INFERIORE (↓)";

        System.out.println("Fase: " + CLIPrinter.ANSI_YELLOW + "RISOLUZIONE AZIONI" + CLIPrinter.ANSI_RESET);
        System.out.println("Azione: Devi pescare dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
        System.out.print("Digita il NUMERO della carta: ");
    }

    /**
     * Delegates the heavy rendering of the game board to the central context.
     */
    @Override
    public void render(UIContext context) {
        context.drawUI();
    }
}