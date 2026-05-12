package it.polimi.ingsw.mesos.view.CLI;

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

    // LA NUOVA MACCHINA A STATI
    private UIState currentState;

    // --- FLAG DI RENDERING ---
    private boolean fullDirty = true;
    private boolean softDirty = false;
    private final Queue<String> notifications = new LinkedList<>();
    private boolean resolutionTimeoutScheduled = false;

    public CLI() {
        this.scanner = new Scanner(System.in);
        this.currentState = new LoginState(); // Stato iniziale!
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
            // Usa il nuovo stato di fine partita!
            transitionTo(new EndGameState());
        }
        else if (isMyTurn()) {
            if (currentGameState.currentState == GameState.PLACING_TOTEMS) {
                transitionTo(new PlacingTotemState());
            }
            else if (currentGameState.currentState == GameState.RESOLVING_ACTIONS) {
                // Check sui nuovi stati per evitare di sovrascriverli a ogni aggiornamento
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
            // Correttissimo: se non è il mio turno, metto la CLI in stato "dormiente"
            transitionTo(WaitingState.INSTANCE);
        }
    }

    private void initializeEventHandlers() {
        eventHandlers.put(UIEvent.GameUpdatedEvent.class, event -> handleGameUpdated((UIEvent.GameUpdatedEvent) event));
        eventHandlers.put(UIEvent.LobbyUpdatedEvent.class, event -> handleLobbyUpdated((UIEvent.LobbyUpdatedEvent) event));
        eventHandlers.put(UIEvent.ClientStateUpdatedEvent.class, event -> handleClientStateUpdated((UIEvent.ClientStateUpdatedEvent) event));
        eventHandlers.put(UIEvent.ActionRejectedEvent.class, event -> handleActionRejected((UIEvent.ActionRejectedEvent) event));
        eventHandlers.put(UIEvent.ActionAcceptedEvent.class, event -> handleActionAccepted((UIEvent.ActionAcceptedEvent) event));
        eventHandlers.put(UIEvent.MessageEvent.class, event -> handleMessage((UIEvent.MessageEvent) event));
        eventHandlers.put(UIEvent.UserInputEvent.class, event -> handleUserInput((UIEvent.UserInputEvent) event));
        eventHandlers.put(UIEvent.ResolutionTimeoutEvent.class, event -> handleResolutionTimeout());
        eventHandlers.put(UIEvent.GameRestoredEvent.class, event -> handleGameRestored((UIEvent.GameRestoredEvent) event));
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

    private void handleLobbyUpdated(UIEvent.LobbyUpdatedEvent e) {
        this.currentLobby = e.lobby();
        if (this.currentClientState == null) {
            this.currentClientState = ClientState.LOBBY;
            transitionTo(new LobbyState());
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
            // Usa il nuovo stato che stampa il messaggio di attesa!
            transitionTo(new WaitingPlayersState());
        }
        else if (e.state() == ClientState.END_GAME) {
            // Usa il nuovo stato che stampa i punteggi!
            transitionTo(new EndGameState());
        }

        this.fullDirty = true;
    }

    private void handleActionRejected(UIEvent.ActionRejectedEvent e) {
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
        printDBLeaderboard();
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

    private void renderIfNeeded() {
        if ((!fullDirty && !softDirty) || currentClientState == null) return;
        if (currentClientState == ClientState.IN_GAME && currentGameState != null && currentGameState.currentState == GameState.FINISHED) return;

        // 1. RENDERING PRINCIPALE DELEGATO ALLO STATO
        if (fullDirty) {
            currentState.render(this);
        }

        // 2. STAMPA NOTIFICHE RIMANENTI
        flushNotifications();

        // 3. GESTIONE PROMPT DI GIOCO
        if (currentClientState == ClientState.IN_GAME && currentGameState != null) {
            if (awaitingServerResponse) {
                if (fullDirty) System.out.println("\n⏳ Mossa inviata, elaborazione del server in corso...");
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

    private void renderLobby() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_CYAN + "=== SALA D'ATTESA (LOBBY) ===" + CLIPrinter.ANSI_RESET);

        if (currentLobby == null || currentLobby.isEmpty()) {
            System.out.println(CLIPrinter.ANSI_GRAY + "Nessuna partita disponibile. Creane una nuova!" + CLIPrinter.ANSI_RESET);
        } else {
            System.out.println("Partite attualmente disponibili:");
            for (LobbyInfoDTO info : currentLobby) {
                System.out.println("▶ ID Partita: " + CLIPrinter.ANSI_YELLOW + info.id + CLIPrinter.ANSI_RESET + " | Giocatori: " + info.numPlayers + "/" + info.maxNumPlayers);
            }
        }

        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Crea una nuova partita");
        System.out.println("2. Unisciti a una partita esistente");
        System.out.print("Scelta (1 o 2): ");
    }


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

    private void printDBLeaderboard() {
        try {
            GameResultDAO dao = new GameResultDAO();
            LeaderboardService service = new LeaderboardService(dao);
            int numPlayers = currentGameState.players.size();
            List<GameResult> leaderboard = service.getLeaderboard(numPlayers);

            System.out.println(CLIPrinter.ANSI_YELLOW + "\n===== CLASSIFICA COMPLETA (" + numPlayers + " GIOCATORI) =====" + CLIPrinter.ANSI_RESET);
            int pos = 1;
            for (GameResult r : leaderboard) {
                System.out.printf("%d) %s - %d punti | %d giocatori | (%s)\n", pos, r.getNickname(), r.getPoints(), r.getNumPlayers(), r.getDate().toString());
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
}
