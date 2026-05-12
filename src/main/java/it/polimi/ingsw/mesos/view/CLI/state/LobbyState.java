package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

import java.util.List;

public class LobbyState implements UIState {

    private enum Phase { MENU, CHOOSING_PLAYERS, JOINING }
    private Phase currentPhase = Phase.MENU;

    @Override
    public void handleInput(String input, UIContext context) {
        if (currentPhase == Phase.MENU) {
            if (input.equals("1")) {
                System.out.print("Quanti giocatori parteciperanno? (2-5): ");
                this.currentPhase = Phase.CHOOSING_PLAYERS;
            } else if (input.equals("2")) {
                System.out.print("Inserisci l'ID della partita a cui unirti: ");
                this.currentPhase = Phase.JOINING;
            } else {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida. Scrivi 1 o 2." + CLIPrinter.ANSI_RESET);
                System.out.print("Scelta (1 o 2): ");
            }
        }
        else if (currentPhase == Phase.CHOOSING_PLAYERS) {
            try {
                int num = Integer.parseInt(input);
                if (num >= 2 && num <= 5) {
                    context.getController().createNewGame(num);
                    System.out.println(CLIPrinter.ANSI_YELLOW + "Creazione in corso..." + CLIPrinter.ANSI_RESET);
                    context.transitionTo(WaitingState.INSTANCE);
                } else {
                    System.out.println(CLIPrinter.ANSI_RED + "❌ Numero non consentito. Scegli tra 2 e 5." + CLIPrinter.ANSI_RESET);
                    System.out.print("Quanti giocatori parteciperanno? (2-5): ");
                }
            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
                System.out.print("Quanti giocatori parteciperanno? (2-5): ");
            }
        }
        else if (currentPhase == Phase.JOINING) {
            try {
                int gameId = Integer.parseInt(input);
                context.getController().joinGame(gameId);
                System.out.println(CLIPrinter.ANSI_YELLOW + "Accesso in corso..." + CLIPrinter.ANSI_RESET);
                context.transitionTo(WaitingState.INSTANCE);
            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
                System.out.print("Inserisci l'ID della partita a cui unirti: ");
            }
        }
    }

    @Override
    public void render(UIContext context) {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_CYAN + "=== SALA D'ATTESA (LOBBY) ===" + CLIPrinter.ANSI_RESET);

        List<LobbyInfoDTO> lobby = context.getLobby();

        if (lobby == null || lobby.isEmpty()) {
            System.out.println(CLIPrinter.ANSI_GRAY + "Nessuna partita disponibile. Creane una nuova!" + CLIPrinter.ANSI_RESET);
        } else {
            System.out.println("Partite attualmente disponibili:");
            for (LobbyInfoDTO info : lobby) {
                System.out.println("▶ ID Partita: " + CLIPrinter.ANSI_YELLOW + info.id + CLIPrinter.ANSI_RESET +
                        " | Giocatori: " + info.numPlayers + "/" + info.maxNumPlayers);
            }
        }
        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Crea una nuova partita");
        System.out.println("2. Unisciti a una partita esistente");
        System.out.print("Scelta (1 o 2): ");
    }
}
