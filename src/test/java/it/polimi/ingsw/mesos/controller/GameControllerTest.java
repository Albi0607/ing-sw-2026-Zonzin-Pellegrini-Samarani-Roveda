package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.model.state.EventState;
import it.polimi.ingsw.mesos.model.state.FinishedState;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.model.state.ResolvingState;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;
import it.polimi.ingsw.mesos.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController controller;
    private VirtualView mockView;

    @BeforeEach
    void setUp() {
        controller = new GameController(1);
        mockView = new VirtualView() {
            @Override public void sendGame(GameDTO gameDTO) {}
            @Override public void sendClientState(ClientState clientState) {}
            @Override public void showMessage(String message) {}
            @Override public String getNickname() { return "Test"; }
            @Override public void sendLobby(List<LobbyInfoDTO> lobby) {}
            @Override public String getId() {return "";}
            @Override public void showActionRejected(String reason) {};
            @Override public void showActionAccepted(String message) {}
            @Override public void showLoginError(String message) {}

            ;
        };
    }

    @Test
    void testSimulazionePrimoRoundCompleto() {

        controller.setNumPlayers(3);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob",Color.RED,mockView);
        controller.addPlayer("Carlo",Color.PURPLE, mockView);

        controller.startGame();

        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());

        // --- 2. FASE DI PIAZZAMENTO (PLACING) ---
        // Recuperiamo l'ordine di turno deciso dal modello
        List<Player> actingOrder = controller.getGame().getPlayers(); // O l'ordine della track

        char[] tilesToPick = {'D', 'E', 'F'};
        for (int i = 0; i < actingOrder.size(); i++) {
            String currentNickname = actingOrder.get(i).getNickname();
            controller.onPlaceTotem(currentNickname, tilesToPick[i]);
        }

        // Dopo l'ultimo piazzamento, il gioco deve essere passato automaticamente alla risoluzione
        assertEquals(GameState.RESOLVING_ACTIONS, controller.getGame().getCurrentState().getStateId());

        // --- 3. FASE DI RISOLUZIONE (RESOLVING) ---
        // Risolviamo i giocatori uno alla volta finché ci sono pick pendenti
        while (controller.getGame().getCurrentState().getStateId() == GameState.RESOLVING_ACTIONS) {

            // Chiediamo al modello chi è il giocatore che deve agire ora
            ResolvingState rs = (ResolvingState) controller.getGame().getCurrentState();
            Player activePlayer = rs.getActivePlayer(controller.getGame());

            // Se il giocatore ha ancora carte da prendere, ne prende una dalla fila sotto (indice 0)
            // Usiamo un while interno per svuotare i pick di quel giocatore
            if (activePlayer != null) {
                Board board = controller.getGame().getBoard();

                // Determiniamo se dobbiamo pescare sopra o sotto
                boolean pickUpper = rs.getRemainingUpper() > 0;
                List<Card> row = pickUpper ? board.getUpperRow() : board.getLowerRow();

                // --- FIX: Cerchiamo l'indice della prima carta che NON sia un evento ---
                int validIndex = -1;
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).getAsEventCard() == null) {
                        validIndex = i;
                        break;
                    }
                }

                if (validIndex != -1) {
                    controller.onTakeCard(activePlayer.getNickname(), validIndex, pickUpper);
                }
            }
        }

        // --- 4. FASE EVENTI E AVANZAMENTO (EVENT -> SETUP) ---
        // Se la risoluzione è finita, il gioco deve essere passato per gli eventi
        // e poi essere arrivato al SETUP o direttamente al PLACING del Round 2.

        GameState finalState = controller.getGame().getCurrentState().getStateId();

        // Se il tuo SetupState passa automaticamente al PlacingState del Round 2:
        assertEquals(GameState.PLACING_TOTEMS, finalState,
                "Il gioco dovrebbe essere tornato in fase di piazzamento per il Round 2");

        // Verifichiamo che i giocatori siano tornati sulla TurnOrderTrack
        assertFalse(controller.getGame().getBoard().getTurnOrderTrack().getPositions().isEmpty(),
                "La track non dovrebbe essere vuota all'inizio del Round 2");
    }


    @Test
    void testControllerLifecycleAndSecurityFailsafes() {
        // --- 1. TEST DEI LIMITI DI SETUP ---

        // Numero giocatori non valido
        assertThrows(IllegalArgumentException.class, () -> controller.setNumPlayers(1),
                "Il controller deve rifiutare 1 giocatore");
        assertThrows(IllegalArgumentException.class, () -> controller.setNumPlayers(6),
                "Il controller deve rifiutare 6 giocatori");

        controller.setNumPlayers(3);

        // Aggiunta giocatori con nomi non validi
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer("", Color.RED, mockView),
                "Il controller deve rifiutare stringhe vuote");
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer(null, Color.BLUE, mockView),
                "Il controller deve rifiutare nomi null");

        // Aggiunta corretta
        controller.addPlayer("Marco",Color.PURPLE, mockView);
        controller.addPlayer("Sofia",Color.BLUE, mockView);

        // Duplicati
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer("Marco", Color.RED, mockView),
                "Il controller deve rifiutare nomi duplicati");

        // Ultimo giocatore (innesca la creazione automatica)
        controller.addPlayer("Alice",Color.WHITE, mockView);
        assertNotNull(controller.getGame(), "Il gioco deve essere stato istanziato automaticamente");

        // Limite massimo superato
        assertThrows(IllegalStateException.class, () -> controller.addPlayer("Bob",Color.YELLOW, mockView),
                "Il controller deve bloccare aggiunte a gioco già creato");



        // Il gioco è appena stato creato ed è in PLACING_TOTEMS.
        // Proviamo a fare un'azione che richiede RESOLVING_ACTIONS (pescare una carta).
        // Il controller DEVE bloccarci per lo stato scorretto
        controller.onTakeCard("Marco", 0, true);

        // Verifichiamo che l'azione sia stata effettivamente ignorata e che
        // il gioco sia RIMASTO saldo nella fase di piazzamento!
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId(),
                "L'azione di pescata doveva essere ignorata dal controller");

        // Avvio del gioco (se fa setup aggiuntivi della board)
        controller.startGame();
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());


        // --- 3. TEST FASE DI PIAZZAMENTO (PLACING_TOTEMS) ---

        // Azione sbagliata nello stato corrente
        controller.onTakeCard("Marco", 0, true);

        // Verifichiamo che il gioco non sia andato avanti, e che Marco non abbia pescato nulla!
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId(),
                "Il controller deve ignorare la pescata durante il piazzamento");

        // Il controller deve respingere giocatori non registrati restituendo FALSE
        boolean esito1 = controller.onPlaceTotem("GiocatoreFantasma", 'A');
        assertFalse(esito1, "Il controller deve respingere giocatori non registrati ritornando false");

        // Il controller deve respingere tessere inesistenti restituendo FALSE
        boolean esito2 = controller.onPlaceTotem("Marco", 'Z');
        assertFalse(esito2, "Il controller deve respingere tessere non valide ritornando false");

        boolean resultFakePlayer = controller.onPlaceTotem("GiocatoreFantasma", 'A');
        assertFalse(resultFakePlayer, "Il controller deve respingere giocatori non registrati (restituendo false)");

        // Tessera inesistente
        boolean resultFakeTile = controller.onPlaceTotem("Marco", 'Z');
        assertFalse(resultFakeTile, "Il controller deve respingere tessere non valide (restituendo false)");

        // Esecuzione di piazzamenti legali in modo dinamico (ignora l'ordine casuale)
        List<Player> turnOrder = controller.getGame().getPlayers(); // O la TurnOrderTrack se accessibile
        char[] availableTiles = {'C', 'D', 'E'};

        for (int i = 0; i < 3; i++) {
            String activeNickname = turnOrder.get(i).getNickname(); // Sostituisci con il metodo corretto per sapere a chi tocca piazzare

            // Verifichiamo che il piazzamento funzioni senza lanciare eccezioni
            final char tileToPlace = availableTiles[i];
            assertDoesNotThrow(() -> controller.onPlaceTotem(activeNickname, tileToPlace));
        }

        // --- 4. TRANSIZIONE E FASE DI RISOLUZIONE (RESOLVING_ACTIONS) ---

        // Dopo l'ultimo piazzamento, lo stato deve essere cambiato
        assertEquals(GameState.RESOLVING_ACTIONS, controller.getGame().getCurrentState().getStateId());

        // Azione sbagliata nel nuovo stato
        boolean esitoPiazzamentoRitardato = controller.onPlaceTotem("Marco", 'A');
        assertFalse(esitoPiazzamentoRitardato, "Non si possono piazzare totem durante la risoluzione (deve ritornare false)");



        System.out.println("Stress Test del Controller completato con successo: Barriere di sicurezza intatte.");
    }

    @Test
    void testOnSkipExtraDraw_WrongState_ReturnsFalse() {
        // Prepariamo la partita ma NON avanziamo fino a RESOLVING_ACTIONS
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.RED, mockView);
        controller.addPlayer("Bob", Color.WHITE, mockView);
        controller.startGame();

        // Siamo ancora in PLACING_TOTEMS
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());

        // Alice prova a saltare un'azione fuori fase
        boolean result = controller.onSkipExtraDraw("Alice");

        assertFalse(result, "onSkipExtraDraw deve ritornare false se chiamato nello stato sbagliato");
    }

    private void avanzaFinoARisoluzioneAzioni() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.PURPLE, mockView );

        // NOTA: Ho tolto controller.startGame() perché il tuo addPlayer lo chiama
        // in automatico non appena entra l'ultimo giocatore (la stanza si riempie).

        // Ci sono 2 giocatori, quindi dobbiamo fare 2 piazzamenti validi
        for (int i = 0; i < 2; i++) {
            // 1. Chiediamo al gioco a chi tocca esattamente in questo momento
            String currentNickname = controller.getGame().getCurrentPlayerNickname();
            assertNotNull(currentNickname, "Il giocatore corrente non dovrebbe essere null");

            // 2. Chiediamo alla board quale è la prima tessera offerta ancora libera
            char availableTile = controller.getGame().getBoard().getTiles().stream()
                    .filter(t -> t.getHost() == null) // Prendiamo solo quelle libere
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Nessuna tessera libera trovata!"))
                    .getId();

            // 3. Facciamo la mossa legalmente
            boolean success = controller.onPlaceTotem(currentNickname, availableTile);
            assertTrue(success, "Il piazzamento del totem dovrebbe andare a buon fine");
        }
    }

    class FakeView implements VirtualView {
        private final List<String> messages = new ArrayList<>();

        @Override public void sendGame(GameDTO gameDTO) {}
        @Override public void sendClientState(ClientState clientState) {}
        @Override public void showMessage(String message) { messages.add(message); }
        @Override public String getNickname() { return "fake"; }
        @Override public void sendLobby(List<LobbyInfoDTO> lobby) {}
        @Override public String getId() { return ""; }
        @Override public void showActionRejected(String reason) {};
        @Override public void showActionAccepted(String message) {}
        @Override public void showLoginError(String message) {}

        public List<String> getMessages() { return messages; }
    }

    @Test
    void testEndGame_SavesResultsAndSendsMessages() throws Exception {

        String username = "root" ;
        String pw = "1234" ;
        DBManager.init(username,pw);
        GameResultDAO dao = new GameResultDAO();
        LeaderboardService service = new LeaderboardService(dao);
        dao.clearAll();

        GameController controller = new GameController(1);
        controller.setLeaderboardService(service);

        FakeView v1 = new FakeView();
        FakeView v2 = new FakeView();
        FakeView v3 = new FakeView();

        controller.setNumPlayers(3);
        controller.addPlayer("Alice", Color.PURPLE, v1);
        controller.addPlayer("Bob", Color.BLUE, v2);
        controller.addPlayer("Carlo", Color.RED, v3);

        controller.startGame();

        // Imposto punteggi finali
        controller.getGame().getPlayers().stream()
                .filter(p -> p.getNickname().equals("Alice"))
                .findFirst().get().setPrestigePoints(100);

        controller.getGame().getPlayers().stream()
                .filter(p -> p.getNickname().equals("Bob"))
                .findFirst().get().setPrestigePoints(80);

        controller.getGame().getPlayers().stream()
                .filter(p -> p.getNickname().equals("Carlo"))
                .findFirst().get().setPrestigePoints(120);

        controller.endGame();

        assertTrue(v1.getMessages().stream().anyMatch(m -> m.contains("posizione: 2")));
        assertTrue(v2.getMessages().stream().anyMatch(m -> m.contains("posizione: 3")));
        assertTrue(v3.getMessages().stream().anyMatch(m -> m.contains("posizione: 1")));

        var leaderboard = service.getLeaderboard(3);
        assertEquals(3, leaderboard.size());
    }

    @Test
    void testEndGame_UniversalTrigger() throws Exception {
        String username = "root" ;
        String pw = "1234" ;
        DBManager.init(username,pw);
        GameResultDAO dao = new GameResultDAO();
        LeaderboardService service = new LeaderboardService(dao);
        dao.clearAll();

        GameController controller = new GameController(1);
        controller.setLeaderboardService(service);

        FakeView v1 = new FakeView();
        FakeView v2 = new FakeView();

        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.WHITE, v1);
        controller.addPlayer("Bob", Color.RED, v2);

        controller.startGame();

        // --- Condizione di fine partita ---
        controller.getGame().setCurrentRound(10);

        // --- TRIGGER NATURALE ---
        controller.getGame().changeState(controller.getGame().getCurrentState());

        // --- Verifica ---
        assertTrue(v1.getMessages().stream().anyMatch(m -> m.contains("posizione")));
        assertTrue(v2.getMessages().stream().anyMatch(m -> m.contains("posizione")));

        var leaderboard = service.getLeaderboard(2);
        assertEquals(2, leaderboard.size());
    }

    @Test
    void testReplayModeBasic() {
        controller.setReplayMode(true);
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);

        assertNotNull(controller.getGame());
    }

    @Test
    void testGettersAndPersistence() {
        assertEquals(0, controller.getExpectedNumPlayers());
        assertEquals(0, controller.getNumPlayersConnected());
        assertNotNull(controller.getMoveLogger());
        assertNotNull(controller.getStateSerializer());
        assertFalse(controller.hasRestorer());

        controller.setNumPlayers(2);
        assertEquals(2, controller.getExpectedNumPlayers());

        controller.addPlayer("Alice", Color.BLUE, mockView);
        assertEquals(1, controller.getNumPlayersConnected());
        assertEquals(List.of(Color.BLUE), controller.getTakenColors());

        assertNotNull(controller.getLeaderboardService());
        assertNotNull(controller.getLeaderboardService());
    }

    @Test
    void testOnGameFinishedCallback() throws SQLException {
        boolean[] called = {false};
        controller.setOnGameFinished(() -> called[0] = true);

        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        controller.endGame();
        assertTrue(called[0]);
    }

    @Test
    void testReconnectionFlow() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        controller.onPlayerDisconnected("Alice");

        VirtualView newView = new VirtualView() {
            @Override public void sendGame(GameDTO gameDTO) {}
            @Override public void sendClientState(ClientState clientState) {}
            @Override public void showMessage(String message) {}
            @Override public String getNickname() { return "Alice"; }
            @Override public void sendLobby(List<LobbyInfoDTO> lobby) {}
            @Override public String getId() { return ""; }
            @Override public void showActionRejected(String reason) {}
            @Override public void showActionAccepted(String message) {}
            @Override public void showLoginError(String message) {}
        };

        controller.reconnectPlayer("Alice", newView);

        // Test reconnecting unknown player
        assertThrows(IllegalArgumentException.class, () -> controller.reconnectPlayer("Unknown", mockView));
    }

    @Test
    void testDisconnectionAndSkipTurn() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        String currentPlayer = controller.getGame().getCurrentPlayerNickname();
        controller.onPlayerDisconnected(currentPlayer);

        assertNotEquals(currentPlayer, controller.getGame().getCurrentPlayerNickname());

        // Test disconnecting already disconnected player
        controller.onPlayerDisconnected(currentPlayer);
    }

    @Test
    void testActionsWhenDisconnected() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        String currentPlayer = controller.getGame().getCurrentPlayerNickname();
        controller.onPlayerDisconnected(currentPlayer);

        assertFalse(controller.onPlaceTotem(currentPlayer, 'A'));
        assertFalse(controller.onTakeCard(currentPlayer, 0, true));
        assertFalse(controller.onSkipExtraDraw(currentPlayer));
    }

    @Test
    void testAllPlayersDisconnected() throws SQLException {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        controller.onPlayerDisconnected("Alice");
        controller.onPlayerDisconnected("Bob");
    }

    @Test
    void testTurnTimerFlow() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        controller.startTurnTimer("Alice");
        controller.cancelTurnTimer();
    }

    @Test
    void testSetNumPlayers_Logging() {
        // ExpectedNumPlayers is 0 initially.
        controller.setNumPlayers(3);
        
        List<GameMove> moves = controller.getMoveLogger().readAll();
        boolean foundSet = moves.stream()
                .anyMatch(m -> m.type == GameMove.MoveType.SET_NUM_PLAYERS && m.intPayload == 3);
        assertTrue(foundSet, "La mossa SET_NUM_PLAYERS dovrebbe essere presente nel log");
    }

    @Test
    void testSetNumPlayers_TriggersBroadcastInThread() throws InterruptedException {
        // We use a latch or just wait a bit to ensure the thread inside setNumPlayers runs
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        
        controller.setNumPlayers(2);
        
        // Wait a brief moment for the thread to execute v.sendClientState and broadcastUpdate
        Thread.sleep(200);
        
        assertNotNull(controller.getGame());
    }

    @Test
    void testSetNumPlayersAlreadySet() {
        controller.setNumPlayers(2);
        // This should trigger the "already set" block
        controller.setNumPlayers(3);
        assertEquals(2, controller.getExpectedNumPlayers());
    }

    @Test
    void testSetNumPlayersTriggersCreateGame() {
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        // Alice and Bob are already in pendingNicknames.
        // Setting numPlayers to 2 should trigger createGame() and startGame()
        controller.setNumPlayers(2);
        assertNotNull(controller.getGame());
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());
    }

    @Test
    void testOnSkipExtraDraw_Success() {
        avanzaFinoARisoluzioneAzioni();

        // Ci assicuriamo di essere nello stato corretto
        assertEquals(GameState.RESOLVING_ACTIONS, controller.getGame().getCurrentState().getStateId());

        // Dobbiamo capire a quale giocatore tocca eseguire la risoluzione ora
        String current = controller.getGame().getCurrentPlayerNickname();
        assertNotNull(current, "Ci dovrebbe essere un giocatore attivo in risoluzione");

        // Il giocatore attivo decide di saltare
        boolean result = controller.onSkipExtraDraw(current);

        assertTrue(result, "onSkipExtraDraw deve ritornare true quando l'azione è legale");
    }

    @Test
    void testOnSkipExtraDraw_FullExecutionPath() {
        avanzaFinoARisoluzioneAzioni();
        String current = controller.getGame().getCurrentPlayerNickname();

        // Start timer to verify cancelTurnTimer() is called (implicitly by the path execution)
        controller.startTurnTimer(current);

        // Perform the skip
        boolean result = controller.onSkipExtraDraw(current);

        assertTrue(result);
        
        // Verify move was logged
        List<GameMove> moves = controller.getMoveLogger().readAll();
        boolean foundSkip = moves.stream()
                .anyMatch(m -> m.type == GameMove.MoveType.SKIP_EXTRA_DRAW && current.equals(m.nickname));
        assertTrue(foundSkip, "La mossa SKIP_EXTRA_DRAW dovrebbe essere presente nel log");
    }

    @Test
    void testOnSkipExtraDraw_PlayerDisconnected_ReturnsFalse() {
        avanzaFinoARisoluzioneAzioni();
        String current = controller.getGame().getCurrentPlayerNickname();

        // Simulate disconnection
        controller.onPlayerDisconnected(current);

        // Try to skip
        boolean result = controller.onSkipExtraDraw(current);

        assertFalse(result, "Dovrebbe ritornare false se il giocatore è disconnesso");
    }

    @Test
    void testOnSkipExtraDraw_PlayerNotFound_ReturnsFalse() {
        avanzaFinoARisoluzioneAzioni();

        // Proviamo a chiamare l'azione con un nome che non fa parte del gioco
        boolean result = controller.onSkipExtraDraw("CarloFantasma");

        assertFalse(result, "onSkipExtraDraw deve ritornare false se il giocatore non esiste");
    }

    @Test
    void testAddPlayer_ReplayMode() {
        controller.setReplayMode(true);
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        // Should not create game yet
        assertNull(controller.getGame());
        controller.addPlayer("Bob", Color.RED, mockView);
        // Should create game now because pendingNicknames.size() == 2
        assertNotNull(controller.getGame());
    }

    @Test
    void testAddPlayer_RestorerFlow_WaitingPlayers() throws IOException {
        String logFile = "test_restorer_wait.log";
        Files.deleteIfExists(Paths.get(logFile));
        MoveLogger logger = new MoveLogger(logFile);

        // Add TWO players to the log
        logger.append(GameMove.addPlayer("Alice", Color.BLUE));
        logger.append(GameMove.addPlayer("Bob", Color.RED));

        GameRestorer restorer = new GameRestorer(logger);
        controller.setRestorer(restorer);

        // First player reconnects
        controller.addPlayer("Alice", Color.BLUE, mockView);

        // Should still have restorer because Bob hasn't reconnected
        assertTrue(controller.hasRestorer());

        Files.deleteIfExists(Paths.get(logFile));
    }

    @Test
    void testAddPlayer_RestorerFlow_Complete() throws IOException {
        String logFile = "test_restorer_complete.log";
        Files.deleteIfExists(Paths.get(logFile));
        MoveLogger logger = new MoveLogger(logFile);

        logger.append(GameMove.addPlayer("Alice", Color.BLUE));

        GameRestorer restorer = new GameRestorer(logger);
        controller.setRestorer(restorer);

        controller.addPlayer("Alice", Color.BLUE, mockView);

        // Reconnection complete
        assertFalse(controller.hasRestorer());

        Files.deleteIfExists(Paths.get(logFile));
    }

    @Test
    void testAddPlayer_RestorerFlow_GameAlreadyCreated() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        GameRestorer restorer = new GameRestorer(new MoveLogger("temp.log"));
        controller.setRestorer(restorer);

        // Bob reconnects (game != null, restorer != null)
        controller.addPlayer("Bob", Color.RED, mockView);
        
        // Alice reconnects -> should complete restoration
        controller.addPlayer("Alice", Color.BLUE, mockView);
    }

    @Test
    void testEndGame_DBInactive() throws SQLException {
        // Ensure DB is not active or just let it fail gracefully
        // The controller catches exceptions or checks DBManager.isActive()
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();
        
        // We can just call endGame. If DB is inactive it skips DB logic.
        assertDoesNotThrow(() -> controller.endGame());
    }

    @Test
    void testBroadcastUpdate_Exceptions() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, new VirtualView() {
            @Override public void sendGame(GameDTO gameDTO) { throw new RuntimeException("Network Error"); }
            @Override public void sendClientState(ClientState clientState) {}
            @Override public void showMessage(String message) {}
            @Override public String getNickname() { return "Alice"; }
            @Override public void sendLobby(List<LobbyInfoDTO> lobby) {}
            @Override public String getId() { return ""; }
            @Override public void showActionRejected(String reason) {}
            @Override public void showActionAccepted(String message) {}
            @Override public void showLoginError(String message) {}
        });
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();
        
        // broadcastUpdate is called inside startGame and other actions.
        // It should catch the RuntimeException.
        assertDoesNotThrow(() -> controller.broadcastUpdate());
    }

    @Test
    void testOnPlayerDisconnected_AllConnectedCountZero() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", Color.BLUE, mockView);
        controller.addPlayer("Bob", Color.RED, mockView);
        controller.startGame();

        controller.onPlayerDisconnected("Alice");
        // This will call skipDisconnectedTurn which will check connectedCount
        controller.onPlayerDisconnected("Bob");
    }
}

