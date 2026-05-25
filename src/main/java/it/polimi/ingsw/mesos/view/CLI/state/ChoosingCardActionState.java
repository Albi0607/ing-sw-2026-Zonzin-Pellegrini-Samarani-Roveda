package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

/**
 * Represents the local sub-menu UI state triggered when an active player possesses
 * a special building granting an optional extra draw.
 * It allows the user to decide whether to proceed with the extra draw or skip it entirely.
 */
public class ChoosingCardActionState implements UIState {

    /**
     * Processes the user's menu choice (1 to Draw, 2 to Skip).
     * Option 1 triggers a local transition to the card selection state without network interaction.
     * Option 2 dispatches the skip command to the server and locks the UI.
     *
     * @param input   The raw string typed by the user (expected "1" or "2").
     * @param context The UIContext providing access to state transitions and the network controller.
     */
    @Override
    public void handleInput(String input, UIContext context) {

        if (context.getGameState().currentState != GameState.RESOLVING_ACTIONS) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: La fase delle azioni è terminata." + CLIPrinter.ANSI_RESET);
            context.transitionTo(WaitingState.INSTANCE);
            return;
        }

        if (input.equals("1")) {
            // context.setAwaitingServerResponse(true);
            context.transitionTo(new ChoosingCardIdState());
            context.getCurrentState().renderPrompt(context);

        } else if (input.equals("2")) {
            context.setAwaitingServerResponse(true);
            context.getController().skipOnExtraDraw();

            context.transitionTo(WaitingState.INSTANCE);

        } else {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida! Riprova." + CLIPrinter.ANSI_RESET);
            System.out.print("Scelta (1 o 2): ");
        }
    }

    /**
     * Prints the lightweight prompt presenting the user with the two available actions.
     * Dynamically displays which row (UPPER/LOWER) the draw would be from.
     */
    @Override
    public void renderPrompt(UIContext context) {
        boolean isUpper = context.getGameState().isUpper;
        String nomeFila = isUpper ? "SUPERIORE (↑)" : "INFERIORE (↓)";

        System.out.println("Fase: " + CLIPrinter.ANSI_YELLOW + "RISOLUZIONE AZIONI" + CLIPrinter.ANSI_RESET);
        System.out.println("Hai l'edificio speciale! Scegli un'azione:");
        System.out.println("1. Pesca dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
        System.out.println("2. Salta la pescata extra");
        System.out.print("Scelta (1 o 2): ");
    }

    /**
     * Delegates the heavy rendering of the game board to the central context.
     */
    @Override
    public void render(UIContext context) {
        context.drawUI();
    }
}