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
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

public class CLI implements View {
    private ClientController controller;
    private final Scanner scanner;
    private String myNickname;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // --- LA CODA DEGLI EVENTI CENTRALIZZATA ---
    private final BlockingQueue<UIEvent> eventQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    // --- LO STATO DELLA UI ---
    private GameDTO currentGameState;
    private ClientState currentClientState;
    private List<LobbyInfoDTO> currentLobby;
    private InputMode currentInputMode = InputMode.LOGIN;
    private boolean awaitingServerResponse = false;
    private final Map<Class<? extends UIEvent>, Consumer<UIEvent>> eventHandlers = new HashMap<>();

    // --- FLAG DI RENDERING ---
    private boolean fullDirty = true;
    private boolean softDirty = false;
    private final Queue<String> notifications = new LinkedList<>();
    private boolean resolutionTimeoutScheduled = false;

    public enum InputMode {
        LOGIN, LOBBY_MENU, CHOOSING_NUM_PLAYERS, JOINING_GAME,
        PLACING_TOTEM, CHOOSING_CARD_ACTION, CHOOSING_CARD_ID, WAITING
    }

    public CLI() {
        this.scanner = new Scanner(System.in);
        initializeEventHandlers();
    }

    /**
     * Starts a daemon thread that continuously listens for system console input
     * and wraps it into UserInputEvents for the central queue.
     */
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

    /**
     * Sets the network controller to allow the CLI to send commands to the server.
     * @param controller The ClientController instance.
     */
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    // ==========================================================
    // METODI CHIAMATI DALLA RETE (Inseriscono solo eventi)
    // ==========================================================

    /**
     * Receives a game state update from the network and enqueues a GameUpdatedEvent.
     * @param game The Data Transfer Object containing the current game state.
     */
    @Override
    public void showLastUpdate(GameDTO game) {
        eventQueue.offer(new UIEvent.GameUpdatedEvent(game));
    }

    /**
     * Receives a client state transition and enqueues a ClientStateUpdatedEvent.
     * @param state The new state of the client (LOBBY, IN_GAME, etc.).
     */
    @Override
    public void showClientStateUpdate(ClientState state) {eventQueue.offer(new UIEvent.ClientStateUpdatedEvent(state));}

    /**
     * Enqueues a generic informative message to be displayed as a notification.
     * @param message The text to display.
     */
    @Override
    public void showMessage(String message) {
        eventQueue.offer(new UIEvent.MessageEvent(message));
    }

    /**
     * Receives the list of available game lobbies and enqueues a LobbyUpdatedEvent.
     * @param lobby A list of DTOs representing available game sessions.
     */
    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) {
        eventQueue.offer(new UIEvent.LobbyUpdatedEvent(lobby));
    }

    /**
     * Enqueues an error notification when a requested action is denied by the server.
     * @param reason The explanation for the rejection.
     */
    @Override
    public void showActionRejected(String reason) {
        eventQueue.offer(new UIEvent.ActionRejectedEvent(reason));
    }

    /**
     * Enqueues a success notification when an action is successfully processed.
     * @param message The success message.
     */
    @Override
    public void showActionAccepted(String message) {
        eventQueue.offer(new UIEvent.ActionAcceptedEvent(message));
    }

    // ==========================================================
    // IL MOTORE PRINCIPALE (EDT - Event Dispatch Thread)
    // ==========================================================

    /**
     * Main execution loop of the CLI. Initializes the input thread and
     * processes events from the blocking queue until the application stops.
     */
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
        scheduler.shutdownNow();
    }

    /**
     * Updates the internal input mode based on the current game phase
     * and whether it is the local player's turn.
     */
    private void syncInputModeWithGameState() {
        if (currentGameState.currentState == GameState.FINISHED) {
            this.currentInputMode = InputMode.WAITING;
        }
        else if (isMyTurn()) {
            if (currentGameState.currentState == GameState.PLACING_TOTEMS) {
                this.currentInputMode = InputMode.PLACING_TOTEM;
            }
            else if (currentGameState.currentState == GameState.RESOLVING_ACTIONS) {
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

    private void initializeEventHandlers() {
        eventHandlers.put(
                UIEvent.GameUpdatedEvent.class,
                event -> handleGameUpdated((UIEvent.GameUpdatedEvent) event)
        );

        eventHandlers.put(
                UIEvent.LobbyUpdatedEvent.class,
                event -> handleLobbyUpdated((UIEvent.LobbyUpdatedEvent) event)
        );

        eventHandlers.put(
                UIEvent.ClientStateUpdatedEvent.class,
                event -> handleClientStateUpdated((UIEvent.ClientStateUpdatedEvent) event)
        );

        eventHandlers.put(
                UIEvent.ActionRejectedEvent.class,
                event -> handleActionRejected((UIEvent.ActionRejectedEvent) event)
        );

        eventHandlers.put(
                UIEvent.ActionAcceptedEvent.class,
                event -> handleActionAccepted((UIEvent.ActionAcceptedEvent) event)
        );

        eventHandlers.put(
                UIEvent.MessageEvent.class,
                event -> handleMessage((UIEvent.MessageEvent) event)
        );

        eventHandlers.put(
                UIEvent.UserInputEvent.class,
                event -> handleUserInput((UIEvent.UserInputEvent) event)
        );

        eventHandlers.put(
                UIEvent.ResolutionTimeoutEvent.class,
                event -> handleResolutionTimeout()
        );

        eventHandlers.put(
                UIEvent.GameRestoredEvent.class,
                event -> handleGameRestored((UIEvent.GameRestoredEvent) event)
        );
    }

    /**
     * Core logic for handling different types of UIEvent. Updates internal
     * state variables and manages transitions between game phases.
     * @param event The UIEvent to process.
     */
    private void handleEvent(UIEvent event) {
        Consumer<UIEvent> handler =
                eventHandlers.get(event.getClass());

        if (handler != null) {
            handler.accept(event);
        }
    }

    private void handleGameUpdated(UIEvent.GameUpdatedEvent e) {

        this.currentGameState = e.game();

        if (this.currentClientState == null ||
                this.currentClientState == ClientState.LOBBY ||
                this.currentClientState == ClientState.WAITING_PLAYERS) {

            this.currentClientState = ClientState.IN_GAME;
            this.notifications.clear();
            this.resolutionTimeoutScheduled = false;
        }

        this.awaitingServerResponse = false;

        syncInputModeWithGameState();

        this.fullDirty = true;
    }

    private void handleLobbyUpdated(UIEvent.LobbyUpdatedEvent e) {

        this.currentLobby = e.lobby();
        if (this.currentClientState == null) {
            this.currentClientState = ClientState.LOBBY;
            this.currentInputMode = InputMode.LOBBY_MENU;
        }
        this.fullDirty = true;

    }

    private void handleClientStateUpdated(UIEvent.ClientStateUpdatedEvent e) {

        this.currentClientState = e.state();

        if (e.state() == ClientState.LOBBY) {

            this.currentInputMode = InputMode.LOBBY_MENU;

            this.resolutionTimeoutScheduled = false;

            this.notifications.clear();

            this.currentGameState = null;

        }
        else if (e.state() == ClientState.WAITING_PLAYERS) {

            this.currentInputMode = InputMode.WAITING;
        }

        this.fullDirty = true;
    }

    private void handleActionRejected(UIEvent.ActionRejectedEvent e) {

        if (currentInputMode == InputMode.WAITING &&
                currentClientState == ClientState.IN_GAME &&
                isMyTurn()) {

            this.awaitingServerResponse = false;

            syncInputModeWithGameState();
        }

        if (currentGameState != null &&
                currentGameState.currentState == GameState.FINISHED) {
            return;
        }

        notifications.offer("❌ " + e.reason());

        this.softDirty = true;
    }

    private void handleActionAccepted(UIEvent.ActionAcceptedEvent e) {

        if (currentGameState != null &&
                currentGameState.currentState == GameState.FINISHED) {
            return;
        }

        notifications.offer("✔ " + e.message());

        this.softDirty = true;
    }

    private void handleMessage(UIEvent.MessageEvent e) {

        if (currentGameState != null &&
                currentGameState.currentState == GameState.FINISHED) {
            return;
        }

        notifications.offer("ℹ️ " + e.message());

        this.softDirty = true;
    }

    private void handleUserInput(UIEvent.UserInputEvent e) {
        processInput(e.input());
    }


    private void handleResolutionTimeout() {

        CLIPrinter.clearScreen();

        printDBLeaderboard();

        if (currentGameState != null) {
            CLIPrinter.printGameOver(currentGameState);
        }

        this.currentInputMode = InputMode.WAITING;

        this.fullDirty = false;

        this.softDirty = false;
    }

    private void handleGameRestored(UIEvent.GameRestoredEvent e) {

        this.currentGameState = e.game();

        if (this.currentClientState != ClientState.END_GAME) {
            this.currentClientState = ClientState.IN_GAME;
        }

        this.notifications.clear();

        this.awaitingServerResponse = false;

        this.resolutionTimeoutScheduled = false;

        syncInputModeWithGameState();

        notifications.offer("💾 [SISTEMA] Partita ripristinata correttamente dal salvataggio.");

        this.fullDirty = true;

        this.softDirty = true;
    }

    /**
     * Determines if a redraw is necessary and renders the appropriate
     * screen (Lobby, Waiting Room, Game Board, or End Game) based on state flags.
     */
    private void renderIfNeeded() {
        if ((!fullDirty && !softDirty) || currentClientState == null) return;

        if (currentClientState == ClientState.IN_GAME && currentGameState != null && currentGameState.currentState == GameState.FINISHED) {
            return;
        }

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
                case END_GAME:
                    if (!resolutionTimeoutScheduled && currentGameState != null) {

                        drawUI();

                        while (!notifications.isEmpty()) {
                            System.out.println();
                            System.out.println(
                                    CLIPrinter.ANSI_RED +
                                            "🔔 NOTIFICA: " +
                                            notifications.poll() +
                                            CLIPrinter.ANSI_RESET
                            );
                        }

                        System.out.println("\n✨ Calcolo dei punteggi finali in corso... ✨\n");

                        scheduler.schedule(
                                () -> eventQueue.offer(new UIEvent.ResolutionTimeoutEvent()),
                                3,
                                TimeUnit.SECONDS
                        );

                        resolutionTimeoutScheduled = true;
                        this.currentInputMode = InputMode.WAITING;
                    }
                    break;
            }
        }

        while (!notifications.isEmpty()) {
            System.out.println();
            System.out.println(
                    CLIPrinter.ANSI_RED +
                            "🔔 NOTIFICA: " +
                            notifications.poll() +
                            CLIPrinter.ANSI_RESET
            );
        }

        if (currentClientState == ClientState.IN_GAME && currentGameState != null) {
            /*
            if (currentGameState.currentState == GameState.FINISHED) {
                if (!gameOverRendered) {
                    printDBLeaderboard();
                    CLIPrinter.printGameOver(currentGameState);
                    gameOverRendered = true;
                }
                this.currentInputMode = InputMode.WAITING;
            }

            */
            if (currentGameState.currentState == GameState.FINISHED) {
                // Silenzio assoluto. Non stampare nulla e non chiedere input.
                // Stiamo solo aspettando il segnale END_GAME dal server.
            }
            if (isMyTurn()) {
                if (awaitingServerResponse) {
                    // Stampa "Mossa inviata" solo se la plancia è stata appena ridisegnata,
                    if (fullDirty) System.out.println("\n⏳ Mossa inviata, elaborazione del server in corso...");
                } else {
                    renderTurnPrompt();
                }
            }
            else {
                // Stampa l'attesa solo in caso di full redraw
                if (fullDirty && currentGameState.currentPlayerNickname != null) System.out.println("\n⌛ In attesa che " + currentGameState.currentPlayerNickname + " faccia la sua mossa...");
            }
        }

        fullDirty = false;
        softDirty = false;
    }

    // ========================
    // LOGICA DI GESTIONE INPUT
    // ========================

    /**
     * Routes user strings to specific processing methods based on the current InputMode.
     * @param input The raw string entered by the user.
     */
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

    /**
     * Processes input during the initial login phase to set the player's nickname.
     * @param input The nickname string.
     */
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

    /**
     * Handles navigation and game creation/joining commands while in the lobby.
     * @param input The menu choice or game ID.
     */
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

    /**
     * Removes all pending UserInputEvents from the queue to prevent
     * accidental command execution during state transitions.
     */
    private void clearBufferedUserInputs() {
        eventQueue.removeIf(event -> event instanceof UIEvent.UserInputEvent);
    }

    /**
     * Translates user commands into game actions (placing totems or taking cards)
     * and sends them to the server via the controller.
     * @param input The user's game-related choice.
     */
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

    /**
     * Clears the console and renders the lobby menu with the list of available games.
     */
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

    /**
     * Displays context-sensitive prompts to the user when it is their turn to move.
     */
    private void renderTurnPrompt() {
        System.out.println(CLIPrinter.ANSI_CYAN + "\n TOCCA A TE! " + CLIPrinter.ANSI_RESET);

        if (currentInputMode == InputMode.PLACING_TOTEM) {
            System.out.print("Scegli la tessera per il totem (A, B, C, D, E, F, G): ");
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

    /**
     * Full render of the game interface, including event logs, headers,
     * the board, and player statuses.
     */
    private void drawUI() {
        if (currentGameState == null) return;
        CLIPrinter.clearScreen();

        if (currentGameState.lastResolvedEvents != null && !currentGameState.lastResolvedEvents.isEmpty()) {
            CLIPrinter.printEventPhase(currentGameState);
        }

        /*
        if (currentGameState.currentState == GameState.FINISHED) {
            return;
        }
        */

        CLIPrinter.printHeader(currentGameState, false);
        CLIPrinter.printBoard(currentGameState);
        CLIPrinter.printAllPlayersStatus(currentGameState);
        System.out.println("Fase attuale: " + CLIPrinter.ANSI_GREEN + currentGameState.currentState + CLIPrinter.ANSI_RESET + "\n");
    }

    /**
     * Queries the local database to retrieve and display global leaderboard
     * rankings for the current game size.
     */
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

    /**
     * Checks if the local player is currently the active player in the game.
     * @return true if it is the local player's turn, false otherwise.
     */
    private boolean isMyTurn() {
        return currentGameState.currentPlayerNickname != null &&
                currentGameState.currentPlayerNickname.equals(myNickname);
    }

    /**
     * Checks player tribal buildings to see if they possess the specific
     * ability to skip an extra card draw.
     * @return true if the skip action is available.
     */
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
