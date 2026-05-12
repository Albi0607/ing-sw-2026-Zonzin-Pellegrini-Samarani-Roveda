package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;
import it.polimi.ingsw.mesos.view.CLI.UIContext;

public class PlacingTotemState implements UIState {

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
            context.transitionTo(WaitingState.INSTANCE);
        }
    }

    @Override
    public void renderPrompt(UIContext context) {
        System.out.print("Scegli la tessera per il totem (A, B, C, D, E, F, G): ");
    }

    @Override
    public void render(UIContext context) {
        context.drawUI();
    }
}
