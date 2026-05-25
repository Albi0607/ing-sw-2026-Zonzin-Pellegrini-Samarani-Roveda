package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages the UI for the pre-game lobby room.
 * This state acts as an internal Finite State Machine (wizard flow), guiding the user
 * step-by-step to either create a new game or join an existing one, before finally transitioning
 * to the Waiting state and sending the network command.
 */
public class LobbyState implements UIState {

    /**
     * Internal sub-states representing the steps of the lobby wizard.
     */
    private enum Phase {
        /** The main lobby menu (Create or Join). */
        MENU,
        /** Asking the host for the total number of players. */
        CHOOSING_PLAYERS,
        /** Asking the client for the ID of the game to join. */
        JOINING,
        /** The final step, asking the user to pick an available tribe color. */
        CHOOSING_COLOR
    }

    /** The current step in the lobby setup wizard. */
    private Phase currentPhase = Phase.MENU;

    /** Flag indicating whether the user chose to create a new game (true) or join an existing one (false). */
    private boolean isCreating;

    /** * Temporary storage variable.
     * If isCreating is true, holds the desired number of players.
     * If isCreating is false, holds the ID of the target game.
     */
    private int tempGameData;

    /** Tracks the previous amount of available colors to detect live changes when other players join. */
    private int lastAvailableColorsCount = -1;

    /**
     * Processes user input based on the current internal phase of the wizard.
     * Progresses the internal state upon valid input, and dispatches the final network
     * command (create or join) once all required data (e.g., color) has been gathered.
     */
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
                this.tempGameData = Integer.parseInt(input);
                this.currentPhase = Phase.CHOOSING_COLOR;
                this.lastAvailableColorsCount = -1;
                render(context);

            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
                System.out.print("Inserisci l'ID della partita a cui unirti: ");
            }
        }
        else if (currentPhase == Phase.CHOOSING_COLOR) {
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
                System.out.println(CLIPrinter.ANSI_RED + "❌ Colore inesistente!" + CLIPrinter.ANSI_RESET);
                printColorChoice(context);
            }
        }
    }

    /**
     * Renders the main heavy visual elements of the lobby state.
     * Usually prints the list of available games. If the user is currently choosing a color
     * to join a specific game, it dynamically checks for live changes in available colors.
     */
    @Override
    public void render(UIContext context) {
        CLIPrinter.clearScreen();

        if (currentPhase == Phase.CHOOSING_COLOR && !isCreating) {

            List<Color> currentAvailable = getAvailableColors(context);

            if (getAvailableColors(context).isEmpty()) {
                System.out.println(CLIPrinter.ANSI_RED + "⚠️ Attenziona la partita non accetta più giocatori!" + CLIPrinter.ANSI_RESET);
                resetToMenu();
            }else {
                if (lastAvailableColorsCount != -1 && currentAvailable.size() < lastAvailableColorsCount) {
                    System.out.println(CLIPrinter.ANSI_YELLOW + "🔔 NOTIFICA LIVE: Un altro giocatore si è appena unito! Lista colori aggiornata." + CLIPrinter.ANSI_RESET);
                    System.out.println();
                }
                lastAvailableColorsCount = currentAvailable.size();
            }
        }

        if (currentPhase == Phase.CHOOSING_COLOR) {
            System.out.println(CLIPrinter.ANSI_CYAN + "=== SCELTA COLORE ===" + CLIPrinter.ANSI_RESET);
            printColorChoice(context);
            return;
        }

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

        renderPrompt(context);
    }

    /**
     * Helper method to compute the list of colors still available for the targeted game.
     * Parses the central Lobby DTOs to check which colors are already taken.
     */
    private List<Color> getAvailableColors(UIContext context) {
        List<Color> availableColors = new ArrayList<>(Arrays.asList(Color.values()));

        if (!isCreating) {
            List<LobbyInfoDTO> lobby = context.getLobby();
            boolean gameFound = false;

            if (lobby != null) {
                for (LobbyInfoDTO info : lobby) {
                    if (info.id == tempGameData) {
                        gameFound = true;
                        if (info.started || info.numPlayers >= info.maxNumPlayers) {
                            return new ArrayList<>();
                        }
                        if (info.takenColors != null) {
                            availableColors.removeAll(info.takenColors);
                        }
                        break;
                    }
                }
            }
            // Se la partita non esiste più nel DTO del server, colori disponibili: 0.
            if (!gameFound) return new ArrayList<>();
        }
        return availableColors;
    }

    /**
     * Prints the lightweight context-sensitive question at the bottom of the screen,
     * guiding the user on what to type next based on the active phase.
     */
    @Override
    public void renderPrompt(UIContext context) {
        switch (currentPhase) {
            case MENU ->{
                System.out.println("Cosa vuoi fare?");
                System.out.println("1. Crea una nuova partita");
                System.out.println("2. Unisciti a una partita esistente");
                System.out.print("Scelta (1 o 2): ");
            }
            case CHOOSING_PLAYERS -> System.out.print("Quanti giocatori parteciperanno? (2-5): ");
            case JOINING -> System.out.print("Inserisci l'ID della partita a cui unirti: ");
            case CHOOSING_COLOR -> printColorChoice(context);
        }
    }

    /**
     * Helper to render the specific prompt for color selection.
     */
    private void printColorChoice(UIContext context) {
        List<Color> availableColors = getAvailableColors(context);

        String colorString = availableColors.stream()
                .map(Enum::name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Nessuno");

        System.out.println("\nColori disponibili: " + colorString);
        System.out.print("Scegli il tuo colore: ");
    }

    /**
     * Resets the internal state machine back to the initial menu phase.
     * Useful for aborting a setup sequence (e.g., trying to join a full game).
     */
    public void resetToMenu() {
        currentPhase = Phase.MENU;
        isCreating = false;
        tempGameData = -1;
        lastAvailableColorsCount = -1;
    }

    /**
     * Returns whether the wizard is currently in the final step of picking a color.
     */
    public boolean isChoosingColor() {
        return currentPhase == Phase.CHOOSING_COLOR;
    }

    /**
     * Returns whether the wizard is currently configured to create a new game.
     */
    public boolean isCreating() {
        return isCreating;
    }
}