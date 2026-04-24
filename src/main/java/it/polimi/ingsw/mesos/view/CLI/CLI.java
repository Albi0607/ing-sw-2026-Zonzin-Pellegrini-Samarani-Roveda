package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.RMI.client_RMI;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.rete.View;

import java.util.Scanner;

public class CLI implements View {
    private ClientController controller;
    private final Scanner scanner;
    private String myNickname;

    private GameDTO currentGameState;
    private ClientState currentClientState;

    private volatile boolean actionSent = false;
    private volatile boolean boardUpdated = false;
    private volatile boolean waitingPrinted = false;

    public CLI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Updates the local game state, resets action flags, and triggers a board UI refresh.
     * @param game The Data Transfer Object containing the updated game state.
     */
    @Override
    public void showLastUpdate(GameDTO game) {
        this.currentGameState = game;
        this.actionSent = false;
        this.waitingPrinted = false;
        this.boardUpdated = true;
    }

    /**
     * Updates the current client-side state (e.g., waiting, in-game, choosing players).
     * @param state The new state of the client.
     */
    @Override
    public void showClientStateUpdate(ClientState state) {
        this.currentClientState = state;

    }

    /**
     * Displays a notification message from the server to the user.
     * @param message The message to be displayed.
     */
    @Override
    public void showMessage(String message) {
        System.out.println(CLIPrinter.ANSI_RED + "🔔 NOTIFICA: " + message + CLIPrinter.ANSI_RESET);
        this.actionSent = false;
    }

    /**
     * Main execution loop of the CLI. Handles connection setup and
     * manages the UI rendering and turn logic based on game updates.
     */
    public void start() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_YELLOW + "Benvenuto in Mesos!" + CLIPrinter.ANSI_RESET);

        chooseNetwork();
        setupGame();

        while (true) {

            if (boardUpdated) {
                drawUI();
                boardUpdated = false;
            }

            if (currentClientState == ClientState.CHOOSE_PLAYERS) {
                askForPlayers();
            }
            else if (currentClientState == ClientState.IN_GAME && currentGameState != null) {
                if (currentGameState.currentState == GameState.FINISHED) {
                    if (!actionSent) {
                        CLIPrinter.printGameOver(currentGameState);
                        actionSent = true;
                    }
                }
                else {
                    if (isMyTurn()) {
                        waitingPrinted = false;

                        if (!actionSent) {
                            handleTurn();
                        } else {
                            System.out.print("\r⏳ Mossa inviata, elaborazione in corso...          ");
                        }
                    }
                    else {

                        String activePlayer = currentGameState.currentPlayerNickname;

                        if (!waitingPrinted) {
                            System.out.println("\n⌛ In attesa che " + activePlayer + " faccia la sua mossa...");
                            waitingPrinted = true;
                        }
                    }
                }
            }

            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }
    }

    /**
     * Prompts the user to select a network protocol (RMI or Socket)
     * and initializes the ClientController.
     */
    private void chooseNetwork() {
        System.out.println("\nScegli il tipo di connessione:");
        System.out.println("1. RMI");
        System.out.println("2. Socket");
        System.out.print("Scelta (1 o 2): ");

        String choice = scanner.nextLine().trim();
        Network network = null;

        if (choice.equals("1")) {
            System.out.println("Connessione RMI in corso (su localhost)...");
            try {
                network = new client_RMI();
            } catch (Exception e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore RMI: " + e.getMessage() + CLIPrinter.ANSI_RESET);
                chooseNetwork();
                return;
            }
        } else if (choice.equals("2")) {
            System.out.println("Connessione Socket in corso...");
            //TODO implementazione socket
        } else {
            chooseNetwork();
            return;
        }

        this.controller = new ClientController(this, network);
    }

    /**
     * Handles the initial player registration by asking for a nickname.
     */
    private void setupGame() {
        System.out.print("\nInserisci il tuo nickname: ");
        this.myNickname = scanner.nextLine().trim();
        controller.register(myNickname);
        System.out.println(CLIPrinter.ANSI_YELLOW + "In attesa degli altri giocatori o di comunicazioni dal Server..." + CLIPrinter.ANSI_RESET);
    }

    /**
     * Coordinates the printing of the game header, board, and player status
     * using the CLIPrinter.
     */
    private void drawUI() {
        if (currentGameState == null) return;
        CLIPrinter.clearScreen();
        CLIPrinter.printEventPhase(currentGameState);
        CLIPrinter.printHeader(currentGameState, false);
        CLIPrinter.printBoard(currentGameState);
        CLIPrinter.printAllPlayersStatus(currentGameState);
        System.out.println("Fase attuale: " + CLIPrinter.ANSI_GREEN + currentGameState.currentState + CLIPrinter.ANSI_RESET + "\n");
    }

    /**
     * Checks if the current turn belongs to this client's player.
     * @return true if it is the client's turn, false otherwise.
     */
    private boolean isMyTurn() {
        return currentGameState.currentPlayerNickname != null &&
                currentGameState.currentPlayerNickname.equals(myNickname);
    }

    /**
     * Handles the specific logic for the first player to decide the
     * total number of participants for the match.
     */
    private void askForPlayers() {
        System.out.print(CLIPrinter.ANSI_CYAN + "Sei il primo giocatore! Scegli il numero di giocatori (2-5): " + CLIPrinter.ANSI_RESET);
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());

            currentClientState = ClientState.WAITING_CONNECTION;
            controller.choosePlayer(num);
        } catch (NumberFormatException e) {
            System.out.println(CLIPrinter.ANSI_RED + "Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
        }
    }

    /**
     * Manages user input during the player's turn, differentiating between
     * placing totems and taking cards.
     */
    private void handleTurn() {
        System.out.println(CLIPrinter.ANSI_CYAN + " TOCCA A TE! " + CLIPrinter.ANSI_RESET);

        if (currentGameState.currentState == GameState.PLACING_TOTEMS) {
            System.out.print("Scegli la tessera per il totem (A, B, C, D, E, F): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.isEmpty() || input.length() != 1) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Lettera non valida!" + CLIPrinter.ANSI_RESET);
            } else {
                actionSent = true;
                controller.placeTotem(input.charAt(0));
            }
        }
        else if (currentGameState.currentState == GameState.RESOLVING_ACTIONS) {

            boolean isUpper = currentGameState.isUpper;

            String nomeFila = isUpper ? "SUPERIORE (↑)" : "INFERIORE (↓)";

            System.out.println("Fase: " + CLIPrinter.ANSI_YELLOW + "RISOLUZIONE AZIONI" + CLIPrinter.ANSI_RESET);
            System.out.println("Azione: Devi pescare dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
            System.out.print("Digita il NUMERO della carta: ");
            String input = scanner.nextLine().trim();

            try {
                int cardIndex = Integer.parseInt(input) - 1;
                actionSent = true;

                controller.takeCard(cardIndex, isUpper);

            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Numero non valido!" + CLIPrinter.ANSI_RESET);
            }
        }
    }
}