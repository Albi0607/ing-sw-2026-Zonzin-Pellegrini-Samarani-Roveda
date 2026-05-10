package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.View;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CLI implements View {
    private ClientController controller;
    private final Scanner scanner;
    private String myNickname;

    // --- LA CODA DEGLI EVENTI CENTRALIZZATA ---
    private final BlockingQueue<UIEvent> eventQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    // --- LO STATO DELLA UI ---
    private GameDTO currentGameState;
    private ClientState currentClientState;
    private List<LobbyInfoDTO> currentLobby;
    private InputMode currentInputMode = InputMode.LOGIN;
    private boolean awaitingServerResponse = false;

    // --- FLAG DI RENDERING ---
    private boolean fullDirty = true;
    private boolean softDirty = false;
    private String lastNotification = null;
    private boolean gameOverRendered = false;

    public enum InputMode {
        LOGIN, LOBBY_MENU, CHOOSING_NUM_PLAYERS, JOINING_GAME,
        PLACING_TOTEM, CHOOSING_CARD_ACTION, CHOOSING_CARD_ID, WAITING
    }

    public CLI() {
        this.scanner = new Scanner(System.in);
    }

    private void startInputThread() {
        Thread inputThread = new Thread(() -> {
            while (running) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        eventQueue.offer(new UIEvent.UserInputEvent(line));
                    }
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }

    public void setController(ClientController controller) {
        this.controller = controller;
    }

    // ==========================================================
    // METODI CHIAMATI DALLA RETE (Inseriscono solo eventi)
    // ==========================================================

    @Override
    public void showLastUpdate(GameDTO game) {
        eventQueue.offer(new UIEvent.GameUpdatedEvent(game));
    }

    @Override
    public void showClientStateUpdate(ClientState state) {
        eventQueue.offer(new UIEvent.ClientStateUpdatedEvent(state));
    }

    @Override
    public void showMessage(String message) {
        eventQueue.offer(new UIEvent.MessageEvent(message));
    }

    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) {
        eventQueue.offer(new UIEvent.LobbyUpdatedEvent(lobby));
    }

    @Override
    public void showActionRejected(String reason) {
        eventQueue.offer(new UIEvent.ActionRejectedEvent(reason));
    }

    @Override
    public void showActionAccepted(String message) {
        eventQueue.offer(new UIEvent.ActionAcceptedEvent(message));
    }

    // ==========================================================
    // IL MOTORE PRINCIPALE (EDT - Event Dispatch Thread)
    // ==========================================================

    public void start() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_YELLOW + "Benvenuto in Mesos!" + CLIPrinter.ANSI_RESET);

        startInputThread();

        System.out.print("\nInserisci il tuo nickname: ");
        this.currentInputMode = InputMode.LOGIN;

        while (running) {
            try {

                UIEvent event = eventQueue.take();

                handleEvent(event);

                renderIfNeeded();

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void syncInputModeWithGameState() {
        if (currentGameState.currentState == GameState.FINISHED) {
            this.currentInputMode = InputMode.WAITING;
        }
        else if (isMyTurn()) {
            if (currentGameState.currentState == GameState.PLACING_TOTEMS) {
                this.currentInputMode = InputMode.PLACING_TOTEM;
            }
            else if (currentGameState.currentState == GameState.RESOLVING_ACTIONS) {
                // Rispettiamo i sottomenu! Se siamo già scesi di un livello, non resettiamo.
                if (this.currentInputMode != InputMode.CHOOSING_CARD_ID && this.currentInputMode != InputMode.CHOOSING_CARD_ACTION) {
                    if (canSkipExtraDraw()) {
                        this.currentInputMode = InputMode.CHOOSING_CARD_ACTION;
                    } else {
                        this.currentInputMode = InputMode.CHOOSING_CARD_ID;
                    }
                }
            }
        }
        else {
            this.currentInputMode = InputMode.WAITING;
        }
    }

    private void handleEvent(UIEvent event) {
        if (event instanceof UIEvent.GameUpdatedEvent e) {
            this.currentGameState = e.game();
            this.awaitingServerResponse = false;
            syncInputModeWithGameState();
            this.fullDirty = true;
        }
        else if (event instanceof UIEvent.LobbyUpdatedEvent e) {
            this.currentLobby = e.lobby();
            if (this.currentClientState == null) {
                this.currentClientState = ClientState.LOBBY;
                this.currentInputMode = InputMode.LOBBY_MENU;
            }
            this.fullDirty = true;
        }
        else if (event instanceof UIEvent.ClientStateUpdatedEvent e) {
            this.currentClientState = e.state();
            if (e.state() == ClientState.LOBBY) {
                this.currentInputMode = InputMode.LOBBY_MENU;
                this.gameOverRendered = false;
            } else if (e.state() == ClientState.WAITING_PLAYERS) {
                this.currentInputMode = InputMode.WAITING;
            }
            this.fullDirty = true;
        }
        else if (event instanceof UIEvent.ActionRejectedEvent e) {
            this.lastNotification = "❌ " + e.reason();
            if (currentInputMode == InputMode.WAITING && currentClientState == ClientState.IN_GAME && isMyTurn()) {
                this.awaitingServerResponse = false;
                syncInputModeWithGameState();
            }
            this.softDirty = true;
        }
        else if (event instanceof UIEvent.ActionAcceptedEvent e) {
            this.lastNotification = "✔ " + e.message();
            this.softDirty = true;
        }
        else if (event instanceof UIEvent.MessageEvent e) {
            this.lastNotification = "ℹ️ " + e.message();
            this.softDirty = true;
        }
        else if (event instanceof UIEvent.UserInputEvent e) {
            processInput(e.input());
        }
    }

    private void renderIfNeeded() {
        if ((!fullDirty && !softDirty) || currentClientState == null) return;

        if (fullDirty) {
            switch (currentClientState) {
                case LOBBY:
                    renderLobby();
                    break;
                case WAITING_PLAYERS:
                    CLIPrinter.clearScreen();
                    System.out.println(CLIPrinter.ANSI_CYAN + "=== SALA D'ATTESA PARTITA ===" + CLIPrinter.ANSI_RESET);
                    System.out.println("⏳ " + myNickname + ", sei dentro la partita!");
                    System.out.println("In attesa che gli altri giocatori si uniscano per iniziare...");
                    break;
                case IN_GAME:
                    if (currentGameState != null) {
                        drawUI();
                    }
                    break;
            }
        }

        if (lastNotification != null) {
            System.out.println();
            System.out.println(CLIPrinter.ANSI_RED + "🔔 NOTIFICA: " + lastNotification + CLIPrinter.ANSI_RESET);
            lastNotification = null;
        }

        if (currentClientState == ClientState.IN_GAME && currentGameState != null) {
            if (currentGameState.currentState == GameState.FINISHED) {
                if (!gameOverRendered) {
                    printDBLeaderboard();
                    CLIPrinter.printGameOver(currentGameState);
                    gameOverRendered = true;
                }
                this.currentInputMode = InputMode.WAITING;
            }
            else if (isMyTurn()) {
                if (awaitingServerResponse) {
                    // Stampa "Mossa inviata" solo se la plancia è stata appena ridisegnata,
                    if (fullDirty) System.out.println("\n⏳ Mossa inviata, elaborazione del server in corso...");
                } else {
                    renderTurnPrompt();
                }
            }
            else {
                // Stampa l'attesa solo in caso di full redraw
                if (fullDirty) System.out.println("\n⌛ In attesa che " + currentGameState.currentPlayerNickname + " faccia la sua mossa...");
            }
        }

        fullDirty = false;
        softDirty = false;
    }

    // ========================
    // LOGICA DI GESTIONE INPUT
    // ========================

    private void processInput(String input) {
        switch (currentInputMode) {
            case LOGIN:
                processLoginInput(input);
                break;
            case LOBBY_MENU:
            case CHOOSING_NUM_PLAYERS:
            case JOINING_GAME:
                processLobbyInput(input);
                break;
            case PLACING_TOTEM:
            case CHOOSING_CARD_ACTION:
            case CHOOSING_CARD_ID:
                processTurnInput(input);
                break;
            case WAITING:
                //System.out.println(CLIPrinter.ANSI_GRAY + "Attendi il tuo turno..." + CLIPrinter.ANSI_RESET);
                //break;
                return;
        }
    }

    private void processLoginInput(String input) {
        if (input.isEmpty()) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Il nickname non può essere vuoto. Riprova." + CLIPrinter.ANSI_RESET);
            System.out.print("Inserisci il tuo nickname: ");
            return;
        }

        this.myNickname = input;

        try {
            controller.getLobby(myNickname);
            System.out.println(CLIPrinter.ANSI_YELLOW + "Accesso alla Lobby in corso..." + CLIPrinter.ANSI_RESET);
            this.currentInputMode = InputMode.WAITING;
        } catch (Exception e) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Errore durante l'accesso: " + e.getMessage() + CLIPrinter.ANSI_RESET);
            System.out.print("\nInserisci il tuo nickname: ");
        }
    }

    private void processLobbyInput(String input) {
        if (currentInputMode == InputMode.LOBBY_MENU) {
            if (input.equals("1")) {
                System.out.print("Quanti giocatori parteciperanno? (2-5): ");
                this.currentInputMode = InputMode.CHOOSING_NUM_PLAYERS;
            } else if (input.equals("2")) {
                System.out.print("Inserisci l'ID della partita a cui unirti: ");
                this.currentInputMode = InputMode.JOINING_GAME;
            } else {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida. Scrivi 1 o 2." + CLIPrinter.ANSI_RESET);
                System.out.print("Scelta (1 o 2): ");
            }
        }
        else if (currentInputMode == InputMode.CHOOSING_NUM_PLAYERS) {
            try {
                int num = Integer.parseInt(input);
                if (num >= 2 && num <= 5) {
                    controller.createNewGame(num);
                    System.out.println(CLIPrinter.ANSI_YELLOW + "Creazione in corso..." + CLIPrinter.ANSI_RESET);
                    this.currentInputMode = InputMode.WAITING;
                } else {
                    System.out.println(CLIPrinter.ANSI_RED + "❌ Numero non consentito. Scegli tra 2 e 5." + CLIPrinter.ANSI_RESET);
                    System.out.print("Quanti giocatori parteciperanno? (2-5): ");
                }
            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
                System.out.print("Quanti giocatori parteciperanno? (2-5): ");
            }
        }
        else if (currentInputMode == InputMode.JOINING_GAME) {
            try {
                int gameId = Integer.parseInt(input);
                controller.joinGame(gameId);
                System.out.println(CLIPrinter.ANSI_YELLOW + "Accesso in corso..." + CLIPrinter.ANSI_RESET);
                this.currentInputMode = InputMode.WAITING;
            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Inserisci un numero valido!" + CLIPrinter.ANSI_RESET);
                System.out.print("Inserisci l'ID della partita a cui unirti: ");
            }
        }
    }

    private void clearBufferedUserInputs() {
        eventQueue.removeIf(event -> event instanceof UIEvent.UserInputEvent);
    }

    private void processTurnInput(String input) {

        if (awaitingServerResponse) {
            return;
        }

        if (!isMyTurn()) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: Non è più il tuo turno." + CLIPrinter.ANSI_RESET);
            this.currentInputMode = InputMode.WAITING;
            return;
        }

        if (currentInputMode == InputMode.PLACING_TOTEM) {
            if (currentGameState.currentState != GameState.PLACING_TOTEMS) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: La fase dei totem è terminata." + CLIPrinter.ANSI_RESET);
                this.currentInputMode = InputMode.WAITING;
                return;
            }

            String choice = input.toUpperCase();

            if (choice.isEmpty() || choice.length() != 1) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Lettera non valida!" + CLIPrinter.ANSI_RESET);
                System.out.print("Scegli la tessera per il totem (A, B, C, D, E, F): ");
            } else {
                controller.placeTotem(choice.charAt(0));
                this.awaitingServerResponse = true;
                this.currentInputMode = InputMode.WAITING;

                clearBufferedUserInputs();
            }
        }
        else if (currentInputMode == InputMode.CHOOSING_CARD_ACTION) {
            if (currentGameState.currentState != GameState.RESOLVING_ACTIONS) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: La fase delle azioni è terminata." + CLIPrinter.ANSI_RESET);
                this.currentInputMode = InputMode.WAITING;
                return;
            }

            if (input.equals("1")) {
                System.out.print("Digita il NUMERO della carta: ");
                this.currentInputMode = InputMode.CHOOSING_CARD_ID;
            } else if (input.equals("2")) {
                controller.skipOnExtraDraw();
                this.awaitingServerResponse = true;
                this.currentInputMode = InputMode.WAITING;
            } else {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida! Riprova." + CLIPrinter.ANSI_RESET);
                System.out.print("Scelta (1 o 2): ");
            }
        }
        else if (currentInputMode == InputMode.CHOOSING_CARD_ID) {
            if (currentGameState.currentState != GameState.RESOLVING_ACTIONS) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Sincronizzazione: La fase delle azioni è terminata." + CLIPrinter.ANSI_RESET);
                this.currentInputMode = InputMode.WAITING;
                return;
            }

            try {
                int cardIndex = Integer.parseInt(input) - 1;
                boolean isUpper = currentGameState.isUpper;

                controller.takeCard(cardIndex, isUpper);
                this.awaitingServerResponse = true;
                this.currentInputMode = InputMode.WAITING;

                clearBufferedUserInputs();

            } catch (NumberFormatException e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Numero non valido!" + CLIPrinter.ANSI_RESET);
                System.out.print("Digita il NUMERO della carta: ");
            }
        }
    }

    // ================
    // METODI DI STAMPA
    // ================

    private void renderLobby() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_CYAN + "=== SALA D'ATTESA (LOBBY) ===" + CLIPrinter.ANSI_RESET);

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
    }

    private void renderTurnPrompt() {
        System.out.println(CLIPrinter.ANSI_CYAN + "\n TOCCA A TE! " + CLIPrinter.ANSI_RESET);

        if (currentInputMode == InputMode.PLACING_TOTEM) {
            System.out.print("Scegli la tessera per il totem (A, B, C, D, E, F): ");
        }
        else if (currentInputMode == InputMode.CHOOSING_CARD_ACTION) {
            boolean isUpper = currentGameState.isUpper;
            String nomeFila = isUpper ? "SUPERIORE (↑)" : "INFERIORE (↓)";

            System.out.println("Fase: " + CLIPrinter.ANSI_YELLOW + "RISOLUZIONE AZIONI" + CLIPrinter.ANSI_RESET);
            System.out.println("Hai l'edificio speciale! Scegli un'azione:");
            System.out.println("1. Pesca dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
            System.out.println("2. Salta la pescata extra");
            System.out.print("Scelta (1 o 2): ");
        }
        else if (currentInputMode == InputMode.CHOOSING_CARD_ID) {
            boolean isUpper = currentGameState.isUpper;
            String nomeFila = isUpper ? "SUPERIORE (↑)" : "INFERIORE (↓)";

            System.out.println("Fase: " + CLIPrinter.ANSI_YELLOW + "RISOLUZIONE AZIONI" + CLIPrinter.ANSI_RESET);
            System.out.println("Azione: Devi pescare dalla fila " + CLIPrinter.ANSI_YELLOW + nomeFila + CLIPrinter.ANSI_RESET);
            System.out.print("Digita il NUMERO della carta: ");
        }
    }

    private void drawUI() {
        if (currentGameState == null) return;
        CLIPrinter.clearScreen();
        if (currentGameState.lastResolvedEvents != null && !currentGameState.lastResolvedEvents.isEmpty()) {
            CLIPrinter.printEventPhase(currentGameState);
        }
        CLIPrinter.printHeader(currentGameState, false);
        CLIPrinter.printBoard(currentGameState);
        CLIPrinter.printAllPlayersStatus(currentGameState);
        System.out.println("Fase attuale: " + CLIPrinter.ANSI_GREEN + currentGameState.currentState + CLIPrinter.ANSI_RESET + "\n");
    }

    private void printDBLeaderboard() {
        try {
            GameResultDAO dao = new GameResultDAO();
            LeaderboardService service = new LeaderboardService(dao);
            int numPlayers = currentGameState.players.size();
            List<GameResult> leaderboard = service.getLeaderboard(numPlayers);

            System.out.println(CLIPrinter.ANSI_YELLOW + "\n===== CLASSIFICA COMPLETA (" + numPlayers + " GIOCATORI) =====" + CLIPrinter.ANSI_RESET);
            int pos = 1;
            for (GameResult r : leaderboard) {
                System.out.printf("%d) %s - %d punti | %d giocatori | (%s)\n",
                        pos,
                        r.getNickname(),
                        r.getPoints(),
                        r.getNumPlayers(),
                        r.getDate().toString());
                pos++;
            }
            int myPos = service.getPosition(myNickname, numPlayers);
            System.out.println("\nLa tua posizione nella classifica globale: " + myPos);
            System.out.println(CLIPrinter.ANSI_YELLOW + "===========================================\n" + CLIPrinter.ANSI_RESET);
        } catch (Exception e) {
            System.out.println(CLIPrinter.ANSI_RED + "Errore nel caricamento della classifica dal DB: " + e.getMessage() + CLIPrinter.ANSI_RESET);
        }
    }

    private boolean isMyTurn() {
        return currentGameState.currentPlayerNickname != null &&
                currentGameState.currentPlayerNickname.equals(myNickname);
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
}
