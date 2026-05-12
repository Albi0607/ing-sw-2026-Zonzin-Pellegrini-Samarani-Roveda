package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;
import it.polimi.ingsw.mesos.view.CLI.UIContext;

public class WaitingPlayersState implements UIState {
    @Override
    public void handleInput(String input, UIContext context) {}

    @Override
    public void render(UIContext context) {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_CYAN + "=== SALA D'ATTESA PARTITA ===" + CLIPrinter.ANSI_RESET);
        System.out.println("⏳ " + context.getMyNickname() + ", sei dentro la partita!");
        System.out.println("In attesa che gli altri giocatori si uniscano per iniziare...");
    }
}