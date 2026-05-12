package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

public class ChoosingCardActionState implements UIState {

    @Override
    public void handleInput(String input, UIContext context) {
        if (context.getGameState().currentState != GameState.RESOLVING_ACTIONS) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: La fase delle azioni è terminata." + CLIPrinter.ANSI_RESET);
            context.transitionTo(WaitingState.INSTANCE);
            return;
        }

        if (input.equals("1")) {
            context.transitionTo(new ChoosingCardIdState());
            context.getCurrentState().renderPrompt(context);
        } else if (input.equals("2")) {
            context.getController().skipOnExtraDraw();
            context.transitionTo(WaitingState.INSTANCE);
        } else {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida! Riprova." + CLIPrinter.ANSI_RESET);
            System.out.print("Scelta (1 o 2): ");
        }
    }

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

    @Override
    public void render(UIContext context) {
        context.drawUI();
    }
}
