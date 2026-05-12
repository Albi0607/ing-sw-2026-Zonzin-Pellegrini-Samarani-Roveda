package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

public class ChoosingCardIdState implements UIState {

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
            context.transitionTo(WaitingState.INSTANCE);

        } catch (NumberFormatException e) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Numero non valido!" + CLIPrinter.ANSI_RESET);
            renderPrompt(context);
        }
    }

    @Override
    public void renderPrompt(UIContext context) {
        boolean isUpper = context.getGameState().isUpper;
        String nomeFila = isUpper ? "SUPERIORE (↑)" : "INFERIORE (↓)";
        System.out.println("Fase: " + CLIPrinter.ANSI_YELLOW + "RISOLUZIONE AZIONI" + CLIPrinter.ANSI_RESET);
        System.out.println("Azione: Devi pescare dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
        System.out.print("Digita il NUMERO della carta: ");
    }

    @Override
    public void render(UIContext context) {
        context.drawUI();
    }
}