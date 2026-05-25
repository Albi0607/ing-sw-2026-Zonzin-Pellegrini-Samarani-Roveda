package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

/**
 * Represents the UI state when the local player has successfully joined or created a game,
 * but the match has not yet started because the required number of players hasn't been reached.
 */
public class WaitingPlayersState implements UIState {

    /**
     * Intentionally left blank.
     * Acts as an input "sink", ignoring any keyboard input from the user
     * while they wait in the pre-game room.
     *
     * @param input   The raw string typed by the user (discarded).
     * @param context The UIContext providing access to the CLI engine.
     */
    @Override
    public void handleInput(String input, UIContext context) {
    }

    /**
     * Performs a full screen clear and renders the pre-game waiting room interface.
     *
     * @param context The UIContext providing access to the player's data (e.g., nickname).
     */
    @Override
    public void render(UIContext context) {
        CLIPrinter.clearScreen();

        System.out.println(CLIPrinter.ANSI_CYAN + "=== SALA D'ATTESA PARTITA ===" + CLIPrinter.ANSI_RESET);
        System.out.println("⏳ " + context.getMyNickname() + ", sei dentro la partita!");
        System.out.println("In attesa che gli altri giocatori si uniscano per iniziare...");
    }
}