package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

/**
 * Represents the initial entry point of the application.
 * This state is responsible for capturing the user's chosen nickname,
 * validating it locally, and initiating the connection to the server to fetch available lobbies.
 */
public class LoginState implements UIState {

    /**
     * Processes the nickname entered by the user.
     * If valid, it saves the nickname in the CLI memory, sends a network request
     * to fetch the lobbies, and immediately transitions to a passive waiting state.
     *
     * @param input   The nickname typed by the user.
     * @param context The UIContext providing access to CLI memory and the network controller.
     */
    @Override
    public void handleInput(String input, UIContext context) {

        if (input.isEmpty()) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Il nickname non può essere vuoto. Riprova." + CLIPrinter.ANSI_RESET);
            System.out.print("Inserisci il tuo nickname: ");
            return;
        }

        context.setMyNickname(input);

        try {
            context.getController().getLobby(input);
            System.out.println(CLIPrinter.ANSI_YELLOW + "Accesso alla Lobby in corso..." + CLIPrinter.ANSI_RESET);

            context.transitionTo(WaitingState.INSTANCE);

        } catch (Exception e) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Errore durante l'accesso: " + e.getMessage() + CLIPrinter.ANSI_RESET);
            System.out.print("\nInserisci il tuo nickname: ");
        }
    }
}