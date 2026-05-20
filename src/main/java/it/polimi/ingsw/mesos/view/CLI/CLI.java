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

public class CLI implements View, UIContext {
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
    private boolean awaitingServerResponse = false;
    private final Map<Class<? extends UIEvent>, Consumer<UIEvent>> eventHandlers = new HashMap<>();

    private UIState currentState;

    // --- FLAG DI RENDERING ---
    private boolean fullDirty = true;
    private boolean softDirty = false;
    private final Queue<String> notifications = new LinkedList<>();
    private boolean resolutionTimeoutScheduled = false;

    // DB
    private List<GameResult> cachedLeaderboard = null;
    private int cachedMyPosition = -1;

    public CLI() {
        this.scanner = new Scanner(System.in);
        this.currentState = new LoginState();
        initializeEventHandlers();
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
    public void drawUI() { this.drawUIDirect(); } // Rinomina il vecchio private drawUI() in drawUIDirect() per non fare conflitti

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
                renderIfNeeded();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        scheduler.shutdownNow();
    }

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

    private void handleEvent(UIEvent event) {
        Consumer<UIEvent> handler = eventHandlers.get(event.getClass());
        if (handler != null) {
            handler.accept(event);
        }
    }

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

    private void handleLoginError(UIEvent.LoginErrorEvent e) {
        transitionTo(new LoginState());
        this.awaitingServerResponse = false;
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: " + e.message() + CLIPrinter.ANSI_RESET);
        System.out.print("\nInserisci un nuovo nickname: ");

        this.softDirty = false;
        this.fullDirty = false;
    }

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
        if (currentGameState != null && currentGameState.currentState == GameState.FINISHED) return;
        notifications.offer("❌ " + e.reason());
        this.softDirty = true;
    }

    private void handleActionAccepted(UIEvent.ActionAcceptedEvent e) {
        if (currentGameState != null && currentGameState.currentState == GameState.FINISHED) return;
        this.awaitingServerResponse = false;
        notifications.offer("✔ " + e.message());
        this.softDirty = true;

    }

    private void handleMessage(UIEvent.MessageEvent e) {
        if (currentGameState != null && currentGameState.currentState == GameState.FINISHED) return;
        notifications.offer("ℹ️ " + e.message());
        this.softDirty = true;
    }

    private void handleUserInput(UIEvent.UserInputEvent e) {
        // ECCO LA MAGIA DELLO STATE PATTERN! DELEGA PURA!
        currentState.handleInput(e.input(), this);
    }

    private void handleResolutionTimeout() {
        CLIPrinter.clearScreen();
        printDBLeaderboard(cachedLeaderboard, cachedMyPosition);
        if (currentGameState != null) {
            CLIPrinter.printGameOver(currentGameState);
        }
        transitionTo(WaitingState.INSTANCE);
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
        syncStateWithGame();
        notifications.offer("💾 [SISTEMA] Partita ripristinata correttamente dal salvataggio.");
        this.fullDirty = true;
        this.softDirty = true;
    }

    private void handleLeaderboardReady(UIEvent.LeaderboardReadyEvent e) {
        this.cachedLeaderboard = e.leaderboard();
        this.cachedMyPosition = e.myPosition();
    }

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

        // 1. RENDERING PRINCIPALE DELEGATO ALLO STATO
        if (fullDirty) {
            currentState.render(this);
        }else if (softDirty && currentState instanceof LobbyState) {
            currentState.renderPrompt(this);
        }

        // 2. STAMPA NOTIFICHE RIMANENTI
        flushNotifications();

        // 3. GESTIONE PROMPT DI GIOCO
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

    private void printDBLeaderboard(List<GameResult> leaderboard, int myPosition) {
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
        System.out.println("\nLa tua posizione nella classifica globale: " + myPosition);
        System.out.println(CLIPrinter.ANSI_YELLOW + "===========================================\n" + CLIPrinter.ANSI_RESET);
    }

    private boolean isMyTurn() {
        return currentGameState.currentPlayerNickname != null && currentGameState.currentPlayerNickname.equals(myNickname);
    }

    private boolean canSkipExtraDraw() {
        if (currentGameState == null || currentGameState.players == null) return false;
        for (PlayerDTO player : currentGameState.players) {
            if (player.nickname.equals(myNickname) && player.tribe != null && player.tribe.buildings != null) {
                for (CardDTO building : player.tribe.buildings) {
                    if (building.id.equals("BD_20")) return true;
                }
            }
        }
        return false;
    }

    @Override
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {
        eventQueue.offer(new UIEvent.LeaderboardReadyEvent(leaderboard, myPosition));
    }
}
