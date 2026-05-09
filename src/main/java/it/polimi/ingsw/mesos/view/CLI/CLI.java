package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import it.polimi.ingsw.mesos.RMI.client_RMI;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.rete.View;
import it.polimi.ingsw.mesos.socket.clientSocket;

import java.util.List;
import java.util.Scanner;

public class CLI implements View {
    private ClientController controller;
    private final Scanner scanner;
    private String myNickname;

    private GameDTO currentGameState;
    private ClientState currentClientState;

    private List<LobbyInfoDTO> currentLobby;
    private volatile boolean lobbyUpdated = false;

    private volatile boolean actionSent = false;
    private volatile boolean boardUpdated = false;
    private volatile boolean waitingPrinted = false;

    public CLI() {
        this.scanner = new Scanner(System.in);
    }

    public void setController(ClientController controller) {
        this.controller = controller;
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

    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) {
        this.currentLobby = lobby;
        this.lobbyUpdated = true;
        this.actionSent = false;
    }

    /**
     * Main execution loop of the CLI. Handles connection setup and
     * manages the UI rendering and turn logic based on game updates.
     */
    public void start() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_YELLOW + "Benvenuto in Mesos!" + CLIPrinter.ANSI_RESET);

        setupGame();

        while (true) {

            if (boardUpdated) {
                drawUI();
                boardUpdated = false;
            }

            // GESTIONE LOBBY
            if (lobbyUpdated && currentClientState != ClientState.IN_GAME) {
                if (!actionSent) {
                    handleLobby();
                }
                lobbyUpdated = false; // Resettiamo dopo averla mostrata
            }

            // GESTIONE GIOCO
            else if (currentClientState == ClientState.IN_GAME && currentGameState != null) {
                if (currentGameState.currentState == GameState.FINISHED) {
                    if (!actionSent) {
                        printDBLeaderboard();                        CLIPrinter.printGameOver(currentGameState);
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

    private void printDBLeaderboard() {
        try {
            GameResultDAO dao = new GameResultDAO();
            LeaderboardService service = new LeaderboardService(dao);

            // numero di giocatori della partita corrente
            int numPlayers = currentGameState.players.size();

            // classifica filtrata per numero di giocatori
            List<GameResult> leaderboard = service.getLeaderboard(numPlayers);

            System.out.println(CLIPrinter.ANSI_YELLOW +
                    "\n===== CLASSIFICA COMPLETA (" + numPlayers + " GIOCATORI) ====="
                    + CLIPrinter.ANSI_RESET);

            int pos = 1;
            for (GameResult r : leaderboard) {
                System.out.printf(
                        "%d) %s - %d punti | %d giocatori | (%s)\n",
                        pos,
                        r.getNickname(),
                        r.getPoints(),
                        r.getDate().toString()
                );
                pos++;
            }

            // posizione personale
            int myPos = service.getPosition(myNickname, numPlayers);

            System.out.println("\nLa tua posizione nella classifica globale: " + myPos);
            System.out.println(CLIPrinter.ANSI_YELLOW +
                    "===========================================\n"
                    + CLIPrinter.ANSI_RESET);

        } catch (Exception e) {
            System.out.println(CLIPrinter.ANSI_RED +
                    "Errore nel caricamento della classifica dal DB: " + e.getMessage()
                    + CLIPrinter.ANSI_RESET);
        }
    }


    /**
     * Handles the initial player registration by asking for a nickname.
     */
    private void setupGame() {
        System.out.print("\nInserisci il tuo nickname: ");
        this.myNickname = scanner.nextLine().trim();
        controller.getLobby(myNickname);
        System.out.println(CLIPrinter.ANSI_YELLOW + "Accesso alla Lobby in corso..." + CLIPrinter.ANSI_RESET);
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

    private void handleLobby() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_CYAN + "=== SALA D'ATTESA (LOBBY) ===" + CLIPrinter.ANSI_RESET);

        // Stampa la lista delle partite
        if (currentLobby == null || currentLobby.isEmpty()) {
            System.out.println(CLIPrinter.ANSI_GRAY + "Nessuna partita disponibile. Creane una nuova!" + CLIPrinter.ANSI_RESET);
        } else {
            System.out.println("Partite attualmente disponibili:");
            for (LobbyInfoDTO info : currentLobby) {
                System.out.println("▶ ID Partita: " + CLIPrinter.ANSI_YELLOW + info.id + CLIPrinter.ANSI_RESET +
                        " | Giocatori: " + info.numPlayers + "/" + info.maxNumPlayers);
            }
        }

        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Crea una nuova partita");
        System.out.println("2. Unisciti a una partita esistente");
        System.out.print("Scelta (1 o 2): ");

        boolean validChoice = false;
        while (!validChoice) {
            System.out.print("Scelta (1 o 2): ");
            String choice = scanner.nextLine().trim();

            try {
                if (choice.equals("1")) {
                    System.out.print("Quanti giocatori parteciperanno? (2-5): ");
                    int num = Integer.parseInt(scanner.nextLine().trim());

                    if (num >= 2 && num <= 5) {
                        actionSent = true;
                        controller.createNewGame(num);
                        validChoice = true;
                    } else {
                        System.out.println(CLIPrinter.ANSI_RED + "❌ Numero non consentito. Scegli tra 2 e 5." + CLIPrinter.ANSI_RESET);
                    }
                } else if (choice.equals("2")) {
                    System.out.print("Inserisci l'ID della partita a cui unirti: ");
                    int gameId = Integer.parseInt(scanner.nextLine().trim());
                    actionSent = true;
                    controller.joinGame(gameId);
                    validChoice = true;
                } else {
                    System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida. Scrivi 1 o 2." + CLIPrinter.ANSI_RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
            }
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

            if (canSkipExtraDraw()) {
                System.out.println("Hai l'edificio speciale! Scegli un'azione:");
                System.out.println("1. Pesca dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
                System.out.println("2. Salta la pescata extra");
                System.out.print("Scelta (1 o 2): ");

                String scelta = scanner.nextLine().trim();

                if (scelta.equals("1")) {
                    askDraw(isUpper);
                } else if (scelta.equals("2")) {
                    actionSent = true;
                    controller.skipOnExtraDraw();
                } else {
                    System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida! Riprova." + CLIPrinter.ANSI_RESET);
                }
            } else {
                System.out.println("Azione: Devi pescare dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
                askDraw(isUpper);
            }
        }
    }

    private boolean canSkipExtraDraw() {
        if (currentGameState == null || currentGameState.players == null) return false;

        for (PlayerDTO player : currentGameState.players) {
            if (player.nickname.equals(myNickname) && player.tribe != null && player.tribe.buildings != null) {
                for (CardDTO building : player.tribe.buildings) {
                    if (building.id.equals("BD_20")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void askDraw(boolean isUpper) {
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