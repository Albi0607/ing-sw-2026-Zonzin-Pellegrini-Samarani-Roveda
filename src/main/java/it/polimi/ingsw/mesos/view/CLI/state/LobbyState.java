package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyState implements UIState {

    private enum Phase { MENU, CHOOSING_PLAYERS, JOINING, CHOOSING_COLOR }
    private Phase currentPhase = Phase.MENU;
    private boolean isCreating;
    private int tempGameData;

    @Override
    public void handleInput(String input, UIContext context) {
        if (currentPhase == Phase.MENU) {
            if (input.equals("1")) {
                this.isCreating = true;
                this.currentPhase = Phase.CHOOSING_PLAYERS;
                System.out.print("Quanti giocatori parteciperanno? (2-5): ");
            } else if (input.equals("2")) {
                this.isCreating = false;
                this.currentPhase = Phase.JOINING;
                System.out.print("Inserisci l'ID della partita a cui unirti: ");
            } else {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida. Scrivi 1 o 2." + CLIPrinter.ANSI_RESET);
                System.out.print("Scelta (1 o 2): ");
            }
        }
        else if (currentPhase == Phase.CHOOSING_PLAYERS) {
            try {
                int num = Integer.parseInt(input);
                if (num >= 2 && num <= 5) {
                    this.tempGameData = num;
                    this.currentPhase = Phase.CHOOSING_COLOR;
                    printColorChoice(context);
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
                this.tempGameData = Integer.parseInt(input); // Salviamo l'ID, non chiamiamo ancora il server!
                this.currentPhase = Phase.CHOOSING_COLOR;
                printColorChoice(context);
            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
                System.out.print("Inserisci l'ID della partita a cui unirti: ");
            }
        }else if (currentPhase == Phase.CHOOSING_COLOR) {
            try {
                Color chosenColor = Color.valueOf(input.toUpperCase());
                if (isCreating) {
                    context.getController().createNewGame(tempGameData, chosenColor);
                    System.out.println(CLIPrinter.ANSI_YELLOW + "Creazione in corso..." + CLIPrinter.ANSI_RESET);
                } else {
                    context.getController().joinGame(tempGameData, chosenColor);
                    System.out.println(CLIPrinter.ANSI_YELLOW + "Accesso in corso..." + CLIPrinter.ANSI_RESET);
                }
                context.transitionTo(WaitingState.INSTANCE);

            } catch (IllegalArgumentException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Colore non valido!" + CLIPrinter.ANSI_RESET);
                printColorChoice(context);
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

    private List<Color> getAvailableColors(UIContext context) {
        List<Color> availableColors = new ArrayList<>(Arrays.asList(Color.values()));

        if (!isCreating) {
            List<LobbyInfoDTO> lobby = context.getLobby();
            if (lobby != null) {
                for (LobbyInfoDTO info : lobby) {
                    if (info.id == tempGameData && info.takenColors != null) {
                        availableColors.removeAll(info.takenColors);
                        break;
                    }
                }
            }
        }
        return availableColors;
    }

    private void printColorChoice(UIContext context) {
        List<Color> availableColors = getAvailableColors(context);

        // Sostituisce le parentesi quadre della lista con una stringa pulita
        String colorString = availableColors.toString().replaceAll("[\\[\\]]", "");

        System.out.println("\nColori disponibili: " + colorString);
        System.out.print("Scegli il tuo colore: ");
    }
}
