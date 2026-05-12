package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;
import it.polimi.ingsw.mesos.view.CLI.UIContext;

public class LoginState implements UIState {

    @Override
    public void handleInput(String input, UIContext context) {
        if (input.isEmpty()) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Il nickname non può essere vuoto. Riprova." + CLIPrinter.ANSI_RESET);
            System.out.print("Inserisci il tuo nickname: ");
            return;
        }

        // Usiamo il contesto per salvare il nome e chiamare il controller
        context.setMyNickname(input);

        try {
            context.getController().getLobby(input);
            System.out.println(CLIPrinter.ANSI_YELLOW + "Accesso alla Lobby in corso..." + CLIPrinter.ANSI_RESET);

            // Dopo aver mandato la richiesta, andiamo in stato di attesa
            // per evitare che l'utente spammi la tastiera
            context.transitionTo(WaitingState.INSTANCE);

        } catch (Exception e) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Errore durante l'accesso: " + e.getMessage() + CLIPrinter.ANSI_RESET);
            System.out.print("\nInserisci il tuo nickname: ");
        }
    }
}