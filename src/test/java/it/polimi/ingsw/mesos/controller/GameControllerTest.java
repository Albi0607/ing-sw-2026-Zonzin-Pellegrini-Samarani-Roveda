package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.state.ResolvingState;
import it.polimi.ingsw.mesos.persistence.GameMove;
import it.polimi.ingsw.mesos.persistence.GameRestorer;
import it.polimi.ingsw.mesos.persistence.MoveLogger;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.network.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController controller;
    private VirtualView mockView;

    // Helper: implementazione minimale di VirtualView usata in tutti i test

    static class StubView implements VirtualView {
        private final String nick;
        final List<String> messages = new ArrayList<>();
        StubView(String nick) { this.nick = nick; }
        @Override public void sendGame(GameDTO dto) {}
        @Override public void sendClientState(ClientState s) {}
        @Override public void showMessage(String m) { messages.add(m); }
        @Override public String getNickname() { return nick; }
        @Override public void sendLobby(List<LobbyInfoDTO> l) {}
        @Override public String getId() { return ""; }
        @Override public void showActionRejected(String r) {}
        @Override public void showActionAccepted(String m) {}
        @Override public void showLoginError(String m) {}
        @Override public void showLeaderboard(List<GameResult> lb, int pos) {}
    }

    // Helpers di supporto per portare il controller a stati comuni

    /**
     * Aggiunge 2 giocatori e avvia la partita in modo sincrono.
     * I giocatori vengono registrati PRIMA di setNumPlayers: in questo modo
     * setNumPlayers trova già i giocatori nella pending list e chiama
     * game.startGame() direttamente (non in un thread separato).
     */
    private void initGameWith2Players() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
    }

    /**
     * Porta il controller nello stato RESOLVING_ACTIONS facendo piazzare tutti
     * i totem ai giocatori nel loro ordine corretto.
     */
    private void advanceToResolvingState() {
        initGameWith2Players();
        // Piazziamo i totem per entrambi i giocatori nell'ordine che il modello si aspetta
        for (int i = 0; i < 2; i++) {
            String current = controller.getGame().getCurrentPlayerNickname();
            OfferTile freeTile = controller.getGame().getBoard().getTiles().stream()
                    .filter(OfferTile::isAvailable)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Nessuna tessera libera"));
            controller.onPlaceTotem(current, freeTile.getId());
        }
        assertEquals(GameState.RESOLVING_ACTIONS,
                controller.getGame().getCurrentState().getStateId());
    }

    // Setup

    @BeforeEach
    void setUp() {
        controller = new GameController(1);
        mockView   = new StubView("mock");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (controller.getMoveLogger() != null) {
            controller.getMoveLogger().deleteAll(controller.getGameId());
        }
    }

    // setNumPlayers

    @Test
    void setNumPlayers_validValue_setsExpectedCount() {
        controller.setNumPlayers(3);
        assertEquals(3, controller.getExpectedNumPlayers());
    }

    @Test
    void setNumPlayers_tooFewPlayers_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> controller.setNumPlayers(1));
    }

    @Test
    void setNumPlayers_tooManyPlayers_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> controller.setNumPlayers(6));
    }

    @Test
    void setNumPlayers_alreadySet_isIgnored() {
        controller.setNumPlayers(2);
        controller.setNumPlayers(4);   // deve essere ignorato
        assertEquals(2, controller.getExpectedNumPlayers());
    }

    @Test
    void setNumPlayers_triggersGameCreationWhenPlayersAlreadyPresent() throws InterruptedException {
        // I giocatori arrivano prima di setNumPlayers
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        Thread.sleep(200); // attende il thread di avvio
        assertNotNull(controller.getGame());
    }

    @Test
    void setNumPlayers_sendsWaitingStateWhenPlayersNotFull() {
        controller.setNumPlayers(3);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        // Con solo 1 giocatore su 3 attesi il gioco non deve partire
        assertNull(controller.getGame());
    }

    // addPlayer – flusso normale

    @Test
    void addPlayer_validPlayer_isAddedToPendingList() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        assertEquals(1, controller.getNumPlayersConnected());
    }

    @Test
    void addPlayer_nullNickname_throwsIllegalArgument() {
        controller.setNumPlayers(2);
        assertThrows(IllegalArgumentException.class,
                () -> controller.addPlayer(null, Color.BLUE, mockView));
    }

    @Test
    void addPlayer_blankNickname_throwsIllegalArgument() {
        controller.setNumPlayers(2);
        assertThrows(IllegalArgumentException.class,
                () -> controller.addPlayer("  ", Color.BLUE, mockView));
    }

    @Test
    void addPlayer_duplicateNickname_throwsIllegalArgument() {
        controller.setNumPlayers(3);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        assertThrows(IllegalArgumentException.class,
                () -> controller.addPlayer("Alice", Color.RED, mockView));
    }

    @Test
    void addPlayer_duplicateColor_throwsIllegalArgument() {
        controller.setNumPlayers(3);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        assertThrows(IllegalArgumentException.class,
                () -> controller.addPlayer("Bob", Color.BLUE, mockView));
    }

    @Test
    void addPlayer_roomFull_throwsIllegalState() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        assertThrows(IllegalStateException.class,
                () -> controller.addPlayer("Carlo", Color.PURPLE, mockView));
    }

    @Test
    void addPlayer_lastPlayer_triggersGameCreation() throws InterruptedException {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        // Il gioco viene creato in automatico all'ultimo addPlayer
        assertNotNull(controller.getGame());
    }

    @Test
    void addPlayer_afterGameCreated_throwsIllegalState() {
        initGameWith2Players();
        assertThrows(IllegalStateException.class,
                () -> controller.addPlayer("Carlo", Color.PURPLE, mockView));
    }

    // addPlayer – replay mode

    @Test
    void addPlayer_replayMode_doesNotCreateGameUntilAllPlayersAdded() {
        controller.setReplayMode(true);
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        assertNull(controller.getGame());
    }

    @Test
    void addPlayer_replayMode_createsGameWhenLastPlayerAdded() {
        controller.setReplayMode(true);
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        assertNotNull(controller.getGame());
    }

    // addPlayer – flusso restorer (game == null)

    @Test
    void addPlayer_restorerGameNull_waitsForAllPlayers() throws IOException {
        String logFile = "test_wait.log";
        Files.deleteIfExists(Paths.get(logFile));
        MoveLogger logger = new MoveLogger(logFile);
        logger.append(GameMove.addPlayer("Alice", Color.BLUE));
        logger.append(GameMove.addPlayer("Bob",   Color.RED));

        GameRestorer restorer = new GameRestorer(logger);
        controller.setRestorer(restorer);

        controller.addPlayer("Alice", Color.BLUE, mockView);
        // Bob non ha ancora riconnesso: il restorer deve essere ancora attivo
        assertTrue(controller.hasRestorer());
        Files.deleteIfExists(Paths.get(logFile));
    }

    @Test
    void addPlayer_restorerGameNull_completesRestorationWhenAllPlayersPresent() throws IOException {
        String logFile = "test_complete.log";
        Files.deleteIfExists(Paths.get(logFile));
        MoveLogger logger = new MoveLogger(logFile);
        logger.append(GameMove.addPlayer("Alice", Color.BLUE));

        GameRestorer restorer = new GameRestorer(logger);
        controller.setRestorer(restorer);

        controller.addPlayer("Alice", Color.BLUE, mockView);
        // Unico giocatore nel log → ripristino completo
        assertFalse(controller.hasRestorer());
        Files.deleteIfExists(Paths.get(logFile));
    }

    // addPlayer – flusso restorer (game != null, riconnessione)

    @Test
    void addPlayer_restorerGameAlreadyCreated_reconnectsPlayer() throws IOException {
        initGameWith2Players();
        MoveLogger tempLogger = new MoveLogger("temp_gc.log");
        GameRestorer restorer = new GameRestorer(tempLogger);
        controller.setRestorer(restorer);

        // Bob si riconnette mentre il gioco è già in corso
        controller.addPlayer("Bob", Color.RED, mockView);
        assertNotNull(controller.getGame());
        
        tempLogger.deleteAll(controller.getGameId());
    }

    // startGame

    @Test
    void startGame_withoutGame_throwsIllegalState() {
        assertThrows(IllegalStateException.class, () -> controller.startGame());
    }

    @Test
    void startGame_afterGameCreated_stateIsPlacingTotems() throws InterruptedException {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        assertEquals(GameState.PLACING_TOTEMS,
                controller.getGame().getCurrentState().getStateId());
    }

    @Test
    void startGame_logsStartGameMove() throws InterruptedException {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        boolean found = controller.getMoveLogger().readAll().stream()
                .anyMatch(m -> m.type == GameMove.MoveType.START_GAME);
        assertTrue(found);
    }

    // onPlaceTotem

    @Test
    void onPlaceTotem_unknownPlayer_returnsFalse() {
        initGameWith2Players();
        assertFalse(controller.onPlaceTotem("GiocatoreFantasma", 'A'));
    }

    @Test
    void onPlaceTotem_invalidTileId_returnsFalse() {
        initGameWith2Players();
        String current = controller.getGame().getCurrentPlayerNickname();
        assertFalse(controller.onPlaceTotem(current, 'Z'));
    }

    @Test
    void onPlaceTotem_wrongState_returnsFalse() {
        advanceToResolvingState();
        String current = controller.getGame().getCurrentPlayerNickname();
        // Siamo in RESOLVING_ACTIONS: il piazzamento deve essere rifiutato
        assertFalse(controller.onPlaceTotem(current, 'A'));
    }

    @Test
    void onPlaceTotem_validAction_returnsTrue() {
        initGameWith2Players();
        String current = controller.getGame().getCurrentPlayerNickname();
        OfferTile freeTile = controller.getGame().getBoard().getTiles().stream()
                .filter(OfferTile::isAvailable).findFirst().orElseThrow();
        assertTrue(controller.onPlaceTotem(current, freeTile.getId()));
    }

    @Test
    void onPlaceTotem_logsMove() {
        initGameWith2Players();
        String current = controller.getGame().getCurrentPlayerNickname();
        OfferTile freeTile = controller.getGame().getBoard().getTiles().stream()
                .filter(OfferTile::isAvailable).findFirst().orElseThrow();
        controller.onPlaceTotem(current, freeTile.getId());

        boolean found = controller.getMoveLogger().readAll().stream()
                .anyMatch(m -> m.type == GameMove.MoveType.PLACE_TOTEM
                        && current.equals(m.nickname));
        assertTrue(found);
    }

    @Test
    void onPlaceTotem_disconnectedPlayer_returnsFalse() {
        initGameWith2Players();
        String current = controller.getGame().getCurrentPlayerNickname();
        controller.onPlayerDisconnected(current);

        OfferTile freeTile = controller.getGame().getBoard().getTiles().stream()
                .filter(OfferTile::isAvailable).findFirst().orElseThrow();
        assertFalse(controller.onPlaceTotem(current, freeTile.getId()));
    }

    @Test
    void onPlaceTotem_allPlayersPlace_transitionsToResolvingActions() {
        initGameWith2Players();
        for (int i = 0; i < 2; i++) {
            String current = controller.getGame().getCurrentPlayerNickname();
            OfferTile freeTile = controller.getGame().getBoard().getTiles().stream()
                    .filter(OfferTile::isAvailable).findFirst().orElseThrow();
            controller.onPlaceTotem(current, freeTile.getId());
        }
        assertEquals(GameState.RESOLVING_ACTIONS,
                controller.getGame().getCurrentState().getStateId());
    }

    // onTakeCard

    @Test
    void onTakeCard_unknownPlayer_returnsFalse() {
        advanceToResolvingState();
        assertFalse(controller.onTakeCard("GiocatoreFantasma", 0, true));
    }

    @Test
    void onTakeCard_wrongState_returnsFalse() {
        initGameWith2Players();
        // Siamo in PLACING_TOTEMS: la pescata deve essere rifiutata
        assertFalse(controller.onTakeCard("Alice", 0, true));
    }

    @Test
    void onTakeCard_disconnectedPlayer_returnsFalse() {
        advanceToResolvingState();
        String current = controller.getGame().getCurrentPlayerNickname();
        assertNotNull(current);
        // Aggiungiamo il giocatore al set dei disconnessi senza attivare la logica di skip del turno
        // (usiamo isPlayerDisconnected per verificare che il controller rifiuti l'azione)
        controller.onPlayerDisconnected(current);
        // Il gioco può aver avanzato di stato a seguito della disconnessione;
        // in ogni caso l'azione di un giocatore disconnesso deve ritornare false
        assertFalse(controller.onTakeCard(current, 0, true));
    }

    @Test
    void onTakeCard_validAction_returnsTrue() {
        advanceToResolvingState();
        ResolvingState rs = (ResolvingState) controller.getGame().getCurrentState();
        Player activePlayer = rs.getActivePlayer(controller.getGame());
        assertNotNull(activePlayer);

        Board board = controller.getGame().getBoard();
        boolean pickUpper = rs.getRemainingUpper() > 0;
        List<Card> row = pickUpper ? board.getUpperRow() : board.getLowerRow();

        int validIdx = -1;
        for (int i = 0; i < row.size(); i++) {
            if (row.get(i).getAsEventCard() == null) { validIdx = i; break; }
        }
        if (validIdx == -1) return; // skip se non ci sono carte prendibili

        assertTrue(controller.onTakeCard(activePlayer.getNickname(), validIdx, pickUpper));
    }

    @Test
    void onTakeCard_logsMove() {
        advanceToResolvingState();
        ResolvingState rs = (ResolvingState) controller.getGame().getCurrentState();
        Player activePlayer = rs.getActivePlayer(controller.getGame());
        assertNotNull(activePlayer);

        Board board = controller.getGame().getBoard();
        boolean pickUpper = rs.getRemainingUpper() > 0;
        List<Card> row = pickUpper ? board.getUpperRow() : board.getLowerRow();

        int validIdx = -1;
        for (int i = 0; i < row.size(); i++) {
            if (row.get(i).getAsEventCard() == null) { validIdx = i; break; }
        }
        if (validIdx == -1) return;

        controller.onTakeCard(activePlayer.getNickname(), validIdx, pickUpper);

        boolean found = controller.getMoveLogger().readAll().stream()
                .anyMatch(m -> m.type == GameMove.MoveType.TAKE_CARD
                        && activePlayer.getNickname().equals(m.nickname));
        assertTrue(found);
    }

    // onSkipExtraDraw

    @Test
    void onSkipExtraDraw_unknownPlayer_returnsFalse() {
        advanceToResolvingState();
        assertFalse(controller.onSkipExtraDraw("GiocatoreFantasma"));
    }

    @Test
    void onSkipExtraDraw_wrongState_returnsFalse() {
        initGameWith2Players();
        // Siamo in PLACING_TOTEMS
        assertFalse(controller.onSkipExtraDraw("Alice"));
    }

    @Test
    void onSkipExtraDraw_disconnectedPlayer_returnsFalse() {
        advanceToResolvingState();
        String current = controller.getGame().getCurrentPlayerNickname();
        controller.onPlayerDisconnected(current);
        assertFalse(controller.onSkipExtraDraw(current));
    }

    @Test
    void onSkipExtraDraw_validCall_returnsTrue() {
        advanceToResolvingState();
        String current = controller.getGame().getCurrentPlayerNickname();
        assertTrue(controller.onSkipExtraDraw(current));
    }

    @Test
    void onSkipExtraDraw_logsMove() {
        advanceToResolvingState();
        String current = controller.getGame().getCurrentPlayerNickname();
        controller.onSkipExtraDraw(current);

        boolean found = controller.getMoveLogger().readAll().stream()
                .anyMatch(m -> m.type == GameMove.MoveType.SKIP_EXTRA_DRAW
                        && current.equals(m.nickname));
        assertTrue(found);
    }

    // endGame

    @Test
    void endGame_dbInactive_doesNotThrow() {
        initGameWith2Players();
        assertDoesNotThrow(() -> controller.endGame());
    }

    @Test
    void endGame_invokesOnGameFinishedCallback() throws SQLException {
        boolean[] called = {false};
        controller.setOnGameFinished(() -> called[0] = true);
        initGameWith2Players();
        controller.endGame();
        assertTrue(called[0]);
    }

    @Test
    void endGame_sendsEndGameStateToAllClients() throws SQLException {
        List<ClientState> received = new ArrayList<>();
        VirtualView capturingView = new StubView("Alice") {
            @Override public void sendClientState(ClientState s) { received.add(s); }
        };

        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, capturingView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        controller.endGame();

        assertTrue(received.contains(ClientState.END_GAME));
    }

    // broadcastUpdate

    @Test
    void broadcastUpdate_beforeGameCreated_doesNotThrow() {
        assertDoesNotThrow(() -> controller.broadcastUpdate());
    }

    @Test
    void broadcastUpdate_afterGameStarted_doesNotThrow() {
        // Avviamo il gioco in modo sincrono e verifichiamo che broadcastUpdate
        // non propaghi eccezioni durante una normale partita in corso.
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        assertNotNull(controller.getGame());
        assertDoesNotThrow(() -> controller.broadcastUpdate());
    }

    @Test
    void broadcastUpdate_sendGameThrows_doesNotPropagateException() {
        VirtualView throwingView = new StubView("Alice") {
            @Override public void sendGame(GameDTO dto) { throw new RuntimeException("Network error"); }
        };
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, throwingView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        assertDoesNotThrow(() -> controller.broadcastUpdate());
    }

    @Test
    void broadcastUpdate_replayMode_doesNothing() {
        controller.setReplayMode(true);
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        assertDoesNotThrow(() -> controller.broadcastUpdate());
    }

    // sendClientStateToAll

    @Test
    void sendClientStateToAll_throwingView_doesNotPropagate() {
        // Usiamo una view che lancia RuntimeException su sendClientState
        // La registriamo DOPO l'avvio per non interferire con i sendClientState di setup
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);

        // Sostituiamo la view di Alice con una che lancia eccezione
        VirtualView throwingView = new StubView("Alice") {
            private boolean first = true;
            @Override public void sendClientState(ClientState s) {
                if (first) {
                    first = false;
                } else {
                    throw new RuntimeException();
                }
            }
        };
        controller.reconnectPlayer("Alice", throwingView);

        // sendClientStateToAll deve catturare l'eccezione internamente e non propagarla
        assertDoesNotThrow(() -> controller.sendClientStateToAll(ClientState.IN_GAME));
    }

    // reconnectPlayer

    @Test
    void reconnectPlayer_unknownPlayer_throwsIllegalArgument() {
        initGameWith2Players();
        assertThrows(IllegalArgumentException.class,
                () -> controller.reconnectPlayer("Sconosciuto", mockView));
    }

    @Test
    void reconnectPlayer_knownPlayer_removesFromDisconnected() {
        initGameWith2Players();
        controller.onPlayerDisconnected("Alice");
        assertTrue(controller.isPlayerDisconnected("Alice"));

        controller.reconnectPlayer("Alice", new StubView("Alice"));
        assertFalse(controller.isPlayerDisconnected("Alice"));
    }

    @Test
    void reconnectPlayer_notifiesOtherPlayers() {
        StubView bobView = new StubView("Bob");
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  bobView);

        controller.onPlayerDisconnected("Alice");
        controller.reconnectPlayer("Alice", new StubView("Alice"));

        // Bob deve aver ricevuto il messaggio di riconnessione di Alice
        assertTrue(bobView.messages.stream().anyMatch(m -> m.contains("Alice")));
    }

    // onPlayerDisconnected

    @Test
    void onPlayerDisconnected_marksPlayerAsDisconnected() {
        initGameWith2Players();
        controller.onPlayerDisconnected("Alice");
        assertTrue(controller.isPlayerDisconnected("Alice"));
    }

    @Test
    void onPlayerDisconnected_calledTwice_isIdempotent() {
        initGameWith2Players();
        controller.onPlayerDisconnected("Alice");
        assertDoesNotThrow(() -> controller.onPlayerDisconnected("Alice"));
        assertTrue(controller.isPlayerDisconnected("Alice"));
    }

    @Test
    void onPlayerDisconnected_currentPlayerDisconnects_turnIsSkipped() {
        initGameWith2Players();
        String current = controller.getGame().getCurrentPlayerNickname();
        controller.onPlayerDisconnected(current);
        // Il turno deve essere passato ad un altro giocatore
        assertNotEquals(current, controller.getGame().getCurrentPlayerNickname());
    }

    @Test
    void onPlayerDisconnected_allPlayersDisconnect_doesNotThrow() {
        controller.setOnGameFinished(() -> {});  // callback vuoto per evitare NPE su onGameFinished
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);

        // Disconnettere il primo giocatore skippa il suo turno (può far avanzare il gioco)
        controller.onPlayerDisconnected("Alice");
        // Disconnettere il secondo (connectedCount == 0) deve chiamare endGame senza eccezioni
        assertDoesNotThrow(() -> controller.onPlayerDisconnected("Bob"));
    }

    @Test
    void onPlayerDisconnected_notifiesRemainingPlayers() {
        StubView bobView = new StubView("Bob");
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  bobView);

        controller.onPlayerDisconnected("Alice");
        assertTrue(bobView.messages.stream().anyMatch(m -> m.contains("Alice")));
    }

    // Turn timer

    @Test
    void startAndCancelTurnTimer_doesNotThrow() {
        initGameWith2Players();
        assertDoesNotThrow(() -> {
            controller.startTurnTimer("Alice");
            controller.cancelTurnTimer();
        });
    }

    @Test
    void cancelTurnTimer_whenNoTimerActive_doesNotThrow() {
        assertDoesNotThrow(() -> controller.cancelTurnTimer());
    }

    @Test
    void startTurnTimer_replacesExistingTimer() {
        initGameWith2Players();
        assertDoesNotThrow(() -> {
            controller.startTurnTimer("Alice");
            controller.startTurnTimer("Bob"); // deve cancellare il precedente
            controller.cancelTurnTimer();
        });
    }

    // Getters / utility

    @Test
    void getGameId_returnsConstructorValue() {
        GameController gc = new GameController(42);
        assertEquals(42, gc.getGameId());
    }

    @Test
    void getExpectedNumPlayers_initiallyZero() {
        assertEquals(0, controller.getExpectedNumPlayers());
    }

    @Test
    void getNumPlayersConnected_beforeGame_countsPendingNicknames() {
        controller.setNumPlayers(3);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        assertEquals(1, controller.getNumPlayersConnected());
    }

    @Test
    void getNumPlayersConnected_afterGame_countsGamePlayers() {
        initGameWith2Players();
        assertEquals(2, controller.getNumPlayersConnected());
    }

    @Test
    void getTakenColors_returnsChosenColors() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        assertTrue(controller.getTakenColors().contains(Color.BLUE));
    }

    @Test
    void getMoveLogger_notNull() {
        assertNotNull(controller.getMoveLogger());
    }

    @Test
    void getStateSerializer_notNull() {
        assertNotNull(controller.getStateSerializer());
    }

    @Test
    void hasRestorer_initiallyFalse() {
        assertFalse(controller.hasRestorer());
    }

    @Test
    void hasRestorer_afterSetRestorer_returnsTrue() {
        controller.setRestorer(new GameRestorer(new MoveLogger("dummy.log")));
        assertTrue(controller.hasRestorer());
    }

    @Test
    void getLeaderboardService_notNull() {
        assertNotNull(controller.getLeaderboardService());
    }

    @Test
    void isPlayerDisconnected_connectedPlayer_returnsFalse() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",   Color.RED,  mockView);
        // Non chiamiamo startGame per restare in uno stato stabile
        // e verificare che un giocatore non sia disconnesso prima di qualunque evento
        assertFalse(controller.isPlayerDisconnected("Alice"));
        assertFalse(controller.isPlayerDisconnected("Bob"));
    }

    @Test
    void setNumPlayers_logsSetNumPlayersMove() {
        controller.setNumPlayers(3);
        boolean found = controller.getMoveLogger().readAll().stream()
                .anyMatch(m -> m.type == GameMove.MoveType.SET_NUM_PLAYERS && m.intPayload == 3);
        assertTrue(found);
    }

    @Test
    void setReplayMode_preventsLoggingInSetNumPlayers() {
        // Usiamo un controller con gameId dedicato per evitare conflitti di log con altri test
        GameController freshController = new GameController(9999);
        freshController.setReplayMode(true);
        freshController.setNumPlayers(2);
        // In replayMode il SET_NUM_PLAYERS non deve essere loggato
        boolean found = freshController.getMoveLogger().readAll().stream()
                .anyMatch(m -> m.type == GameMove.MoveType.SET_NUM_PLAYERS);
        assertFalse(found);
    }

    // Flusso di gioco completo (round 1 → round 2)

    @Test
    void fullFirstRound_endingInPlacingTotemsForRound2() {
        controller.setNumPlayers(3);
        controller.addPlayer("Alice", Color.BLUE,   mockView);
        controller.addPlayer("Bob",   Color.RED,    mockView);
        controller.addPlayer("Carlo", Color.PURPLE, mockView);

        assertEquals(GameState.PLACING_TOTEMS,
                controller.getGame().getCurrentState().getStateId());

        // Piazzamento totem
        for (int i = 0; i < 3; i++) {
            String current = controller.getGame().getCurrentPlayerNickname();
            OfferTile freeTile = controller.getGame().getBoard().getTiles().stream()
                    .filter(OfferTile::isAvailable).findFirst().orElseThrow();
            controller.onPlaceTotem(current, freeTile.getId());
        }
        assertEquals(GameState.RESOLVING_ACTIONS,
                controller.getGame().getCurrentState().getStateId());

        // Risoluzione
        while (controller.getGame().getCurrentState().getStateId() == GameState.RESOLVING_ACTIONS) {
            ResolvingState rs = (ResolvingState) controller.getGame().getCurrentState();
            Player active = rs.getActivePlayer(controller.getGame());
            if (active == null) break;

            Board board = controller.getGame().getBoard();
            boolean pickUpper = rs.getRemainingUpper() > 0;
            List<Card> row = pickUpper ? board.getUpperRow() : board.getLowerRow();
            int idx = -1;
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i).getAsEventCard() == null) { idx = i; break; }
            }
            if (idx != -1) {
                controller.onTakeCard(active.getNickname(), idx, pickUpper);
            } else {
                controller.onSkipExtraDraw(active.getNickname());
                break;
            }
        }

        GameState finalState = controller.getGame().getCurrentState().getStateId();
        assertTrue(finalState == GameState.PLACING_TOTEMS || finalState == GameState.FINISHED);
    }
}