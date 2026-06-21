package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.View;
import it.polimi.ingsw.mesos.view.CLI.state.*;

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

/**
 * Main engine for the Command Line Interface.
 * Acts as the centralized View, managing the event loop, routing network/user events
 * to specific UI States, and handling the terminal rendering pipeline.
 */
public class CLI implements View, UIContext {

    /** Network controller used to send commands and actions to the server. */
    private ClientController controller;

    /** Scanner used to asynchronously read standard input from the user. */
    private final Scanner scanner;

    /** The chosen nickname of the player running this client. */
    private String myNickname;

    /** Background scheduler used for timed UI events. */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // --- LA CODA DEGLI EVENTI CENTRALIZZATA ---

    /** Thread-safe queue containing all pending events (user inputs, network updates). */
    private final BlockingQueue<UIEvent> eventQueue = new LinkedBlockingQueue<>();

    /** Flag to keep the main event loop running. */
    private volatile boolean running = true;

    // --- LO STATO DELLA UI ---

    /** The latest game state received from the server. */
    private GameDTO currentGameState;

    /** The overarching phase of the client (e.g., LOBBY, IN_GAME, END_GAME). */
    private ClientState currentClientState;

    /** The list of available matches/lobbies fetched from the server. */
    private List<LobbyInfoDTO> currentLobby;

    /** Flag indicating if the client is currently locked waiting for a server response. */
    private boolean awaitingServerResponse = false;

    /** Registry mapping each UIEvent type to its specific handler method. */
    private final Map<Class<? extends UIEvent>, Consumer<UIEvent>> eventHandlers = new HashMap<>();

    /** The active State Pattern instance handling the current specific UI screen. */
    private UIState currentState;

    // --- FLAG DI RENDERING ---

    /** True if the entire screen needs to be cleared and completely redrawn. */
    private boolean fullDirty = true;

    /** True if only the prompt or pending notifications need to be printed, without clearing the screen. */
    private boolean softDirty = false;

    /** Queue of transient notifications (e.g., errors, confirmations) to be displayed on the next render pass. */
    private final Queue<String> notifications = new LinkedList<>();

    /** Prevents multiple end-game timeout events from being scheduled concurrently. */
    private boolean resolutionTimeoutScheduled = false;

    // DB

    /** Cached global leaderboard fetched from the database at the end of the game. */
    private List<GameResult> cachedLeaderboard = null;

    /**
     * Initializes the CLI, setting up the scanner, mapping event handlers,
     * and defaulting the starting state to Login.
     */
    public CLI() {
        this.scanner = new Scanner(System.in);
        this.currentState = new LoginState();
        initializeEventHandlers();
    }

    /**
     * Spawns a daemon thread that continuously listens for terminal input
     * and pushes it into the centralized event queue as a UserInputEvent.
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
     * Binds the network controller to this CLI instance.
     * @param controller The ClientController instance.
     */
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    // ==========================================================
    // IMPLEMENTAZIONE UIContext (I "servizi" offerti agli Stati)
    // ==========================================================

    @Override
    public void transitionTo(UIState newState) {
        this.currentState = newState;
    }

    @Override
    public UIState getCurrentState() {
        return this.currentState;
    }

    @Override
    public ClientController getController() {
        return this.controller;
    }

    @Override
    public void setMyNickname(String nickname) {
        this.myNickname = nickname;
    }

    @Override
    public String getMyNickname() {
        return this.myNickname;
    }

    @Override
    public GameDTO getGameState() {
        return this.currentGameState;
    }

    @Override
    public void clearBufferedUserInputs() {
        eventQueue.removeIf(event -> event instanceof UIEvent.UserInputEvent);
    }

    @Override
    public List<LobbyInfoDTO> getLobby() { return this.currentLobby; }

    @Override
    public ClientState getClientState() { return this.currentClientState; }

    @Override
    public void drawUI() { this.drawUIDirect(); }

    @Override
    public void flushNotifications() {
        while (!notifications.isEmpty()) {
            System.out.println("\n" + CLIPrinter.ANSI_RED + "🔔 NOTIFICA: " + notifications.poll() + CLIPrinter.ANSI_RESET);
        }
    }

    @Override
    public void scheduleResolutionTimeout() {
        if (!resolutionTimeoutScheduled) {
            scheduler.schedule(() -> eventQueue.offer(new UIEvent.ResolutionTimeoutEvent()), 3, TimeUnit.SECONDS);
            resolutionTimeoutScheduled = true;
        }
    }

    @Override
    public void setAwaitingServerResponse(boolean value) {
        this.awaitingServerResponse = value;
        if (value) {
            this.softDirty = true;
        }
    }

    @Override
    public boolean isfullDirty() {
        return fullDirty;
    }


    // ==========================================================
    // METODI CHIAMATI DALLA RETE (Inseriscono solo eventi)
    // ==========================================================

    @Override
    public void showLastUpdate(GameDTO game) { eventQueue.offer(new UIEvent.GameUpdatedEvent(game)); }
    @Override
    public void showClientStateUpdate(ClientState state) { eventQueue.offer(new UIEvent.ClientStateUpdatedEvent(state)); }
    @Override
    public void showMessage(String message) { eventQueue.offer(new UIEvent.MessageEvent(message)); }
    @Override
    public void showLobby(List<LobbyInfoDTO> lobby) { eventQueue.offer(new UIEvent.LobbyUpdatedEvent(lobby)); }
    @Override
    public void showActionRejected(String reason) { eventQueue.offer(new UIEvent.ActionRejectedEvent(reason)); }
    @Override
    public void showActionAccepted(String message) { eventQueue.offer(new UIEvent.ActionAcceptedEvent(message)); }
    @Override
    public void showLoginError(String message){eventQueue.offer(new UIEvent.LoginErrorEvent(message));}

    // ==========================================================
    // IL MOTORE PRINCIPALE (EDT - Event Dispatch Thread)
    // ==========================================================

    /**
     * Starts the main Event Loop. Blocks the current thread, processing UI and network
     * events sequentially, triggering state logic, and rendering the UI when dirty flags are set.
     */
    public void start() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_YELLOW + "Benvenuto in Mesos!" + CLIPrinter.ANSI_RESET);

        startInputThread();
        this.currentState = new LoginState();
        System.out.print("\nInserisci il tuo nickname: ");

        while (running) {
            try {
                UIEvent event = eventQueue.take();
                handleEvent(event);

                while (!eventQueue.isEmpty()) {
                    UIEvent nextEvent = eventQueue.poll();
                    if (nextEvent != null) {
                        handleEvent(nextEvent);
                    }
                }

                renderIfNeeded();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        scheduler.shutdownNow();
    }

    /**
     * Aligns the local active UIState with the central GameState received from the server.
     * Resolves turn-taking logic, pushing the user to active states or passive Waiting states.
     */
    private void syncStateWithGame() {
        if (currentGameState.currentState == GameState.FINISHED) {

            transitionTo(new EndGameState());
        }
        else if (isMyTurn()) {
            if (currentGameState.currentState == GameState.PLACING_TOTEMS) {
                transitionTo(new PlacingTotemState());
            }
            else if (currentGameState.currentState == GameState.RESOLVING_ACTIONS) {

                if (!(currentState instanceof ChoosingCardActionState) && !(currentState instanceof ChoosingCardIdState)) {
                    if (canSkipExtraDraw()) {
                        transitionTo(new ChoosingCardActionState());
                    } else {
                        transitionTo(new ChoosingCardIdState());
                    }
                }
            }
        }
        else {

            transitionTo(WaitingState.INSTANCE);
        }
    }

    /**
     * Registers all event handlers into the routing map, tying specific UIEvents
     * to their respective processing methods.
     */
    private void initializeEventHandlers() {
        eventHandlers.put(UIEvent.LoginErrorEvent.class, event -> handleLoginError((UIEvent.LoginErrorEvent) event));
        eventHandlers.put(UIEvent.GameUpdatedEvent.class, event -> handleGameUpdated((UIEvent.GameUpdatedEvent) event));
        eventHandlers.put(UIEvent.LobbyUpdatedEvent.class, event -> handleLobbyUpdated((UIEvent.LobbyUpdatedEvent) event));
        eventHandlers.put(UIEvent.ClientStateUpdatedEvent.class, event -> handleClientStateUpdated((UIEvent.ClientStateUpdatedEvent) event));
        eventHandlers.put(UIEvent.ActionRejectedEvent.class, event -> handleActionRejected((UIEvent.ActionRejectedEvent) event));
        eventHandlers.put(UIEvent.ActionAcceptedEvent.class, event -> handleActionAccepted((UIEvent.ActionAcceptedEvent) event));
        eventHandlers.put(UIEvent.MessageEvent.class, event -> handleMessage((UIEvent.MessageEvent) event));
        eventHandlers.put(UIEvent.UserInputEvent.class, event -> handleUserInput((UIEvent.UserInputEvent) event));
        eventHandlers.put(UIEvent.ResolutionTimeoutEvent.class, event -> handleResolutionTimeout());
        eventHandlers.put(UIEvent.GameRestoredEvent.class, event -> handleGameRestored((UIEvent.GameRestoredEvent) event));
        eventHandlers.put(UIEvent.LeaderboardReadyEvent.class,  event -> handleLeaderboardReady((UIEvent.LeaderboardReadyEvent) event));
    }

    /**
     * Retrieves the mapped handler for the given event and executes it.
     * @param event The event pulled from the queue.
     */
    private void handleEvent(UIEvent event) {
        Consumer<UIEvent> handler = eventHandlers.get(event.getClass());
        if (handler != null) {
            handler.accept(event);
        }
    }

    /**
     * Processes a full game update from the server, replacing the local GameDTO
     * and flagging the UI for a full redraw.
     */
    private void handleGameUpdated(UIEvent.GameUpdatedEvent e) {
        this.currentGameState = e.game();
        if (this.currentClientState == null || this.currentClientState == ClientState.LOBBY || this.currentClientState == ClientState.WAITING_PLAYERS) {
            this.currentClientState = ClientState.IN_GAME;
            this.notifications.clear();
            this.resolutionTimeoutScheduled = false;
        }
        this.awaitingServerResponse = false;
        syncStateWithGame();
        this.fullDirty = true;
    }

    /**
     * Processes a login rejection, returning the user to the LoginState prompt.
     */
    private void handleLoginError(UIEvent.LoginErrorEvent e) {
        transitionTo(new LoginState());
        this.awaitingServerResponse = false;
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: " + e.message() + CLIPrinter.ANSI_RESET);
        System.out.print("\nInserisci un nuovo nickname: ");

        this.softDirty = false;
        this.fullDirty = false;
    }

    /**
     * Processes a lobby update, caching the active games list and transitioning
     * to the LobbyState if not actively creating/choosing colors.
     */
    private void handleLobbyUpdated(UIEvent.LobbyUpdatedEvent e) {
        this.currentLobby = e.lobby();

        if (this.currentClientState == null) {
            this.currentClientState = ClientState.LOBBY;
            transitionTo(new LobbyState());
        }

        if (currentState instanceof LobbyState lobbyState) {
            if (lobbyState.isChoosingColor() && lobbyState.isCreating()) {
                return;
            }
        }

        this.fullDirty = true;
    }

    /**
     * Processes overarching client phase shifts (e.g., forced back to LOBBY or END_GAME).
     */
    private void handleClientStateUpdated(UIEvent.ClientStateUpdatedEvent e) {
        this.currentClientState = e.state();

        if (e.state() == ClientState.LOBBY) {
            transitionTo(new LobbyState());
            this.resolutionTimeoutScheduled = false;
            this.notifications.clear();
            this.currentGameState = null;
        }
        else if (e.state() == ClientState.WAITING_PLAYERS) {

            transitionTo(new WaitingPlayersState());
        }
        else if (e.state() == ClientState.END_GAME) {

            transitionTo(new EndGameState());
        }

        this.fullDirty = true;
    }

    /**
     * Processes a rejected action from the server, adding the error to the notifications queue
     * and unlocking the local UI to allow retries.
     */
    private void handleActionRejected(UIEvent.ActionRejectedEvent e) {
        if (currentClientState == ClientState.LOBBY &&
                currentState instanceof WaitingState) {

            CLIPrinter.clearScreen();

            System.out.println(
                    CLIPrinter.ANSI_RED +
                            "❌ Accesso fallito: " + e.reason() +
                            CLIPrinter.ANSI_RESET
            );

            LobbyState lobbyState = new LobbyState();
            lobbyState.resetToMenu();

            transitionTo(lobbyState);

            awaitingServerResponse = false;

            fullDirty = true;
            softDirty = false;

            return;
        }


        if (currentState instanceof WaitingState && currentClientState == ClientState.IN_GAME && isMyTurn()) {
            this.awaitingServerResponse = false;
            syncStateWithGame();
        }
        //if (currentGameState != null && currentGameState.currentState == GameState.FINISHED) return;
        notifications.offer("❌ " + e.reason());
        this.softDirty = true;
    }

    /**
     * Processes a successful action acknowledgment, unlocking the UI and enqueuing the success message.
     */
    private void handleActionAccepted(UIEvent.ActionAcceptedEvent e) {
        //if (currentGameState != null && currentGameState.currentState == GameState.FINISHED) return;
        this.awaitingServerResponse = false;
        notifications.offer("✔ " + e.message());
        this.softDirty = true;

    }

    /**
     * Enqueues a generic info message from the server to be printed on the next render.
     */
    private void handleMessage(UIEvent.MessageEvent e) {
        //if (currentGameState != null && currentGameState.currentState == GameState.FINISHED) return;
        notifications.offer("ℹ️ " + e.message());
        this.softDirty = true;
    }

    /**
     * Delegates the raw string input from the terminal directly to the active UIState logic.
     */
    private void handleUserInput(UIEvent.UserInputEvent e) {
        currentState.handleInput(e.input(), this);
    }

    /**
     * Triggered by a background timeout after the game finishes. Clears the board
     * and prints the final leaderboard and game-over screens.
     */
    private void handleResolutionTimeout() {
        CLIPrinter.clearScreen();
        printDBLeaderboard(cachedLeaderboard);
        if (currentGameState != null) {
            CLIPrinter.printGameOver(currentGameState);
        }
        transitionTo(WaitingState.INSTANCE);
        this.fullDirty = false;
        this.softDirty = false;
    }

    /**
     * Handles the payload received when a crashed game is successfully restored from logs.
     */
    private void handleGameRestored(UIEvent.GameRestoredEvent e) {
        this.currentGameState = e.game();
        if (this.currentClientState != ClientState.END_GAME) {
            this.currentClientState = ClientState.IN_GAME;
        }
        this.notifications.clear();
        this.awaitingServerResponse = false;
        this.resolutionTimeoutScheduled = false;
        syncStateWithGame();
        notifications.offer("💾 [SISTEMA] Partita ripristinata correttamente dal salvataggio.");
        this.fullDirty = true;
        this.softDirty = true;
    }

    /**
     * Caches the database leaderboard data to be printed alongside the end-game screen.
     */
    private void handleLeaderboardReady(UIEvent.LeaderboardReadyEvent e) {
        this.cachedLeaderboard = e.leaderboard();
    }

    /**
     * The core rendering pipeline. Checks dirty flags to decide if the screen needs
     * a full redraw (via the active UIState), or just appending pending notifications/prompts.
     */
    private void renderIfNeeded() {
        if ((!fullDirty && !softDirty) || currentClientState == null) return;
        /*
        if (!fullDirty && !softDirty) return;

        if (currentClientState == null) {
            flushNotifications();
            fullDirty = false;
            softDirty = false;
            return;
        }
         */
        if (currentClientState == ClientState.IN_GAME &&
                currentGameState != null &&
                currentGameState.currentState == GameState.FINISHED) return;

        // RENDERING PRINCIPALE DELEGATO ALLO STATO
        if (fullDirty) {
            currentState.render(this);
        }else if (softDirty && currentState instanceof LobbyState) {
            currentState.renderPrompt(this);
        }

        // STAMPA NOTIFICHE RIMANENTI
        flushNotifications();

        // GESTIONE PROMPT DI GIOCO
        if (currentClientState == ClientState.IN_GAME && currentGameState != null) {
            if (awaitingServerResponse) {
                if (softDirty) System.out.println("\n⏳ Mossa inviata, elaborazione del server in corso...");
            } else {
                currentState.renderPrompt(this);
            }
        }

        fullDirty = false;
        softDirty = false;
    }

    // ================
    // METODI DI STAMPA
    // ================

    /**
     * Clears the terminal and orchestrates the CLIPrinter to draw the full game board,
     * headers, and player statuses.
     */
    private void drawUIDirect() {
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

    /**
     * Formats and prints the global database leaderboard.
     * @param leaderboard The list of historic game results.
     */
    private void printDBLeaderboard(List<GameResult> leaderboard) {
        if (leaderboard == null || leaderboard.isEmpty()) {
            System.out.println("Classifica non disponibile.");
            return;
        }
        int numPlayers = currentGameState.players.size();
        System.out.println(CLIPrinter.ANSI_YELLOW + "\n===== CLASSIFICA COMPLETA (" + numPlayers + " GIOCATORI) =====" + CLIPrinter.ANSI_RESET);
        int pos = 1;
        for (GameResult r : leaderboard) {
            System.out.printf("%d) %s - %d punti | %d giocatori | (%s)\n",
                    pos++, r.getNickname(), r.getPoints(), r.getNumPlayers(), r.getDate());
        }
        System.out.println(CLIPrinter.ANSI_YELLOW + "===========================================\n" + CLIPrinter.ANSI_RESET);
    }

    /**
     * Helper to determine if the local client is the current active player in the server's eyes.
     * @return True if it's the client's turn to act.
     */
    private boolean isMyTurn() {
        return currentGameState.currentPlayerNickname != null && currentGameState.currentPlayerNickname.equals(myNickname);
    }

    /**
     * Helper that queries the GameDTO to check if the current active phase allows skipping an extra draw.
     * @return True if the server indicates the extra draw phase is active.
     */
    private boolean canSkipExtraDraw() {
        return currentGameState != null && currentGameState.isExtraDrawPhase;
    }

    @Override
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {
        eventQueue.offer(new UIEvent.LeaderboardReadyEvent(leaderboard));
    }
}
