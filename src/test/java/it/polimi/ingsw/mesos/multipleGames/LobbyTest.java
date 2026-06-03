package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    //I test della lobby sono strettamente legati a serverState e sarebbe difficile testare la lobby
    //in maniera autonoma, per cui tutti i test partono da metodi di serverState per controllare
    //il corretto funzionamento della lobby


    //classi di supporto ai test:

    //virtualView fittizia da poter usare e passare come parametro
    private VirtualView makeView(String id, String nickname) {
        return new VirtualView() {
            @Override public String getId()         { return id; }
            @Override public String getNickname()   { return nickname; }
            @Override public void sendGame(GameDTO gameDTO) {}
            @Override public void sendClientState(ClientState clientState) {}
            @Override public void sendLobby(List<LobbyInfoDTO> lobby) {}
            @Override public void showMessage(String message) {}
            @Override public void showLoginError(String message) {}
            @Override public void showActionRejected(String reason) {}
            @Override public void showActionAccepted(String message) {}
            @Override public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {}
        };
    }

    //VirtualView che cattura l'ultimo lobby ricevuto
    private static class CapturingView implements VirtualView {
        private final String id;
        private final String nickname;
        List<LobbyInfoDTO> lastLobby;

        CapturingView(String id, String nickname) {
            this.id       = id;
            this.nickname = nickname;
        }

        @Override public String getId()       { return id; }
        @Override public String getNickname() { return nickname; }
        @Override public void sendGame(GameDTO gameDTO) {}
        @Override public void sendClientState(ClientState clientState) {}
        @Override public void sendLobby(List<LobbyInfoDTO> lobby) { lastLobby = lobby; }
        @Override public void showMessage(String message) {}
        @Override public void showLoginError(String message) {}
        @Override public void showActionRejected(String reason) {}
        @Override public void showActionAccepted(String message) {}
        @Override public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {}
    }

    //creazione del serverState per ogni test
    private ServerState serverState;

    @BeforeEach
    void setUp() {
        serverState = new ServerState();
    }

    // =========================================================================
    // addViewer
    // =========================================================================


    @Test
    void addViewer() {
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));

        //controllo che non lanci nessuna eccezione cioè che quindi la virtualView è stata aggiunta correttamente
        assertDoesNotThrow(() ->
                serverState.createNewGame("Alberto", 2, Color.RED, "v1")
        );
    }

    //controllo che venga inviata la lobby
    @Test
    void addViewerSendsLobby() {
        CapturingView cv = new CapturingView("v1", "Alberto");
        serverState.getLobby("Alberto", cv);

        //la lista sarà vuota (o con qualcosa dentro se prima altri test hanno creato una partita)
        assertNotNull(cv.lastLobby);
    }

    // =========================================================================
    // removeViewer
    // =========================================================================

    //rimuovo la view e controllo che non ricava più broadcast
    @Test
    void removeViewer() {
        CapturingView cv = new CapturingView("v1", "Alberto");
        serverState.getLobby("Alberto", cv);

        // Azzera lastLobby così possiamo capire se arriva un nuovo aggiornamento
        cv.lastLobby = null;

        // Rimuoviamo il viewer
        serverState.removeConnection("v1");

        // Creiamo una seconda partita con un altro giocatore: deve scatenare broadcast
        VirtualView v2 = makeView("v2", "Anna");
        serverState.getLobby("Anna", v2);
        serverState.createNewGame("Anna", 2, Color.BLUE, "v2");

        // cv non deve aver ricevuto nulla perché era già rimossa
        assertNull(cv.lastLobby);
    }

    // =========================================================================
    // containView
    // =========================================================================

    @Test
    void containViewTrue() {
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        // Verifichiamo tramite createNewGame che usa containView internamente:
        // se containView fosse false la chiamata ritornerebbe senza fare nulla.
        assertDoesNotThrow(() ->
                serverState.createNewGame("Alberto", 2, Color.RED, "v1")
        );
    }

    @Test
    void containViewFalse() {
        VirtualView v1 = makeView("v1", "Alberto");
        serverState.getLobby("Alberto", v1);
        serverState.removeConnection("v1");

        // Dopo la rimozione, createNewGame non deve creare niente (containView = false).
        // Il controller per "Alberto" rimane null.
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");
        assertNull(serverState.getController("Alberto"));
    }

    // =========================================================================
    // createNewGame
    // =========================================================================

    @Test
    void createNewGame() {
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        //controllo che il controller sia associato al nickname
        assertNotNull(serverState.getController("Alberto"));
    }

    @Test
    void createNewGameViewIsRemoved() {
        CapturingView cv = new CapturingView("v1", "Alberto");
        serverState.getLobby("Alberto", cv);

        // Azzera, poi crea il game
        cv.lastLobby = null;
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        // Ora "Alberto" non è più viewer: un broadcast successivo non le arriva
        cv.lastLobby = null;
        VirtualView v2 = makeView("v2", "Mattia");
        serverState.getLobby("Mattia", v2);
        serverState.createNewGame("Mattia", 2, Color.BLUE, "v2");

        assertNull(cv.lastLobby);
    }

    @Test
    void createNewGameTooFewPlayers() {
        final String[] rejected = {null};
        VirtualView v = new VirtualView() {
            @Override public String getId()       { return "v1"; }
            @Override public String getNickname() { return "Alberto"; }
            @Override public void sendGame(GameDTO gameDTO) {}
            @Override public void sendClientState(ClientState cs) {}
            @Override public void sendLobby(List<LobbyInfoDTO> l) {}
            @Override public void showMessage(String m) {}
            @Override public void showLoginError(String m) {}
            @Override public void showActionRejected(String reason) { rejected[0] = reason; }
            @Override public void showActionAccepted(String m) {}
            @Override public void showLeaderboard(List<GameResult> l, int p) {}
        };

        serverState.getLobby("Alberto", v);
        serverState.createNewGame("Alberto", 1, Color.RED, "v1");

        assertNotNull(rejected[0]);
    }

    @Test
    void createNewGameTooManyPlayers() {
        final String[] rejected = {null};
        VirtualView v = new VirtualView() {
            @Override public String getId()       { return "v1"; }
            @Override public String getNickname() { return "Alberto"; }
            @Override public void sendGame(GameDTO gameDTO) {}
            @Override public void sendClientState(ClientState cs) {}
            @Override public void sendLobby(List<LobbyInfoDTO> l) {}
            @Override public void showMessage(String m) {}
            @Override public void showLoginError(String m) {}
            @Override public void showActionRejected(String reason) { rejected[0] = reason; }
            @Override public void showActionAccepted(String m) {}
            @Override public void showLeaderboard(List<GameResult> l, int p) {}
        };

        serverState.getLobby("Alberto", v);
        serverState.createNewGame("Alberto", 6, Color.RED, "v1");

        assertNotNull(rejected[0]);
    }

    @Test
    void createNewGameCheckLobbyState() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        VirtualView creator = makeView("v1", "Alberto");
        serverState.getLobby("Alberto", creator);

        observer.lastLobby = null; // resettiamo prima della creazione

        serverState.createNewGame("Alberto", 3, Color.RED, "v1");

        // broadcast è stato inviato quindi observer.lastLobby deve contenere 1 partita
        assertNotNull(observer.lastLobby);
        assertEquals(1, observer.lastLobby.size());

        LobbyInfoDTO dto = observer.lastLobby.get(0);
        assertEquals(3, dto.maxNumPlayers);
        assertEquals(1, dto.numPlayers);
        assertFalse(dto.started);
    }

    // =========================================================================
    // joinGame
    // =========================================================================

    @Test
    void joinGame() {
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        serverState.getLobby("Luca", makeView("v2", "Luca"));
        serverState.joinGame("Luca", 1, Color.BLUE, "v2");

        assertNotNull(serverState.getController("Luca"));
    }

    @Test
    void joinGameSameControllerAsCreator() {
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        serverState.getLobby("Anna", makeView("v2", "Anna"));
        serverState.joinGame("Anna", 1, Color.BLUE, "v2");

        assertSame(serverState.getController("Alberto"), serverState.getController("Anna"));
    }

    @Test
    void joinGameNonExistentGame() {
        final String[] rejected = {null};
        VirtualView v = new VirtualView() {
            @Override public String getId()       { return "v1"; }
            @Override public String getNickname() { return "Alberto"; }
            @Override public void sendGame(GameDTO g) {}
            @Override public void sendClientState(ClientState cs) {}
            @Override public void sendLobby(List<LobbyInfoDTO> l) {}
            @Override public void showMessage(String m) {}
            @Override public void showLoginError(String m) {}
            @Override public void showActionRejected(String reason) { rejected[0] = reason; }
            @Override public void showActionAccepted(String m) {}
            @Override public void showLeaderboard(List<GameResult> l, int p) {}
        };

        serverState.getLobby("Alberto", v);
        serverState.joinGame("Alberto", 999, Color.RED, "v1"); // ID inesistente

        assertNotNull(rejected[0]);
    }

    @Test
    void joinGameRemovesViewerFromLobby() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 3, Color.RED, "v1");

        CapturingView lucaView = new CapturingView("v2", "Luca");
        serverState.getLobby("Luca", lucaView);

        serverState.joinGame("Luca", 1, Color.BLUE, "v2");

        // Dopo il join resettiamo e forziamo un broadcast
        lucaView.lastLobby = null;
        serverState.getLobby("Mattia", makeView("v3", "Mattia"));

        // getLobby di Mattia ha scatenato un broadcast — Luca non deve averlo ricevuto
        assertNull(lucaView.lastLobby);
    }

    // =========================================================================
    // removeEmptyGames
    // =========================================================================

    /**
     * removeEmptyGames è chiamata internamente da broadcast(); verifichiamo
     * che una partita svuotata (tutti i viewer rimossi prima che iniziasse)
     * non compaia più nella lobby.
     */
    @Test
    void removeEmptyGames_emptyGameDisappearsFromLobby() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        // Alberto si disconnette senza che nessun altro si sia unito
        serverState.removeConnection("v1");
        serverState.removePlayer("Alberto");

        // Forziamo un broadcast tramite getLobby di un nuovo giocatore
        CapturingView cv = new CapturingView("v2", "Anna");
        serverState.getLobby("Anna", cv);

        // La partita vuota deve essere stata rimossa
        assertNotNull(cv.lastLobby);
        assertTrue(cv.lastLobby.stream().noneMatch(dto -> dto.numPlayers == 0 && !dto.started),
                "Partite vuote non avviate non devono comparire in lobby");
    }

    @Test
    void joinGameAlreadyStarted() {
        final String[] rejected = {null};
        VirtualView v3 = new VirtualView() {
            @Override public String getId()       { return "v3"; }
            @Override public String getNickname() { return "Mattia"; }
            @Override public void sendGame(GameDTO g) {}
            @Override public void sendClientState(ClientState cs) {}
            @Override public void sendLobby(List<LobbyInfoDTO> l) {}
            @Override public void showMessage(String m) {}
            @Override public void showLoginError(String m) {}
            @Override public void showActionRejected(String reason) { rejected[0] = reason; }
            @Override public void showActionAccepted(String m) {}
            @Override public void showLeaderboard(List<GameResult> l, int p) {}
        };

        // Crea partita da 2 e riempila
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        serverState.getLobby("Luca", makeView("v2", "Luca"));
        serverState.joinGame("Luca", 1, Color.BLUE, "v2");

        // Ora game != null e hasRestorer == false quindi deve scattare IllegalStateException
        // che ServerState cattura e passa a showActionRejected
        serverState.getLobby("Mattia", v3);
        serverState.joinGame("Mattia", 1, Color.PURPLE, "v3");

        assertNotNull(rejected[0]);
    }

    // =========================================================================
    // removeFinishedGame
    // =========================================================================

    @Test
    void removeFinishedGame_removesGameFromLobby() throws SQLException {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        serverState.getLobby("Mattia", makeView("v2", "Mattia"));
        serverState.joinGame("Mattia", 1, Color.BLUE, "v2");

        GameController controller = serverState.getController("Alberto");
        controller.endGame();

        assertNotNull(observer.lastLobby);
        assertTrue(observer.lastLobby.isEmpty());
    }

    // =========================================================================
    // removeEmptyGames
    // =========================================================================

    //TODO da sistemare removeEmptyGames soprattutto nel gameController cosi da
    //rimuovere veramente le partite senza player
    @Test
    void removeEmptyGames() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        // Alberto si disconnette — 0 giocatori connessi, game non avviato
        serverState.removeConnection("v1");
        serverState.removePlayer("Alberto");

        // Forziamo un broadcast reale tramite createNewGame
        serverState.getLobby("Mattia", makeView("v2", "Mattia"));
        serverState.createNewGame("Mattia", 2, Color.BLUE, "v2");

        // Ora removeEmptyGames è stata chiamata dentro broadcast()
        assertNotNull(observer.lastLobby);
        assertEquals(1, observer.lastLobby.size());
    }

    @Test
    void removeEmptyGamesGameWithPlayersIsNotRemoved() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        // Alberto è ancora connesso — numPlayersConnected > 0
        serverState.getLobby("Mattia", makeView("v2", "Mattia"));

        assertNotNull(observer.lastLobby);
        assertEquals(1, observer.lastLobby.size());
    }

    @Test
    void removeEmptyGamesStartedGameIsNotRemoved() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        // Creiamo una partita da 2 e la avviamo
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        serverState.getLobby("Mattia", makeView("v2", "Mattia"));
        serverState.joinGame("Mattia", 1, Color.BLUE, "v2");

        // game != null → non deve essere rimossa anche se forzassimo un broadcast
        serverState.getLobby("Luca", makeView("v3", "Luca"));

        assertNotNull(observer.lastLobby);
        assertTrue(observer.lastLobby.stream().anyMatch(dto -> dto.started));
    }

    // =========================================================================
    // broadcast
    // =========================================================================

    @Test
    void broadcast() {
        CapturingView cv1 = new CapturingView("v1", "Alberto");
        CapturingView cv2 = new CapturingView("v2", "Luca");

        serverState.getLobby("Alberto", cv1);
        serverState.getLobby("Luca",   cv2);

        // Reset dopo addViewer
        cv1.lastLobby = null;
        cv2.lastLobby = null;

        // Creiamo una partita con un terzo giocatore → broadcast
        serverState.getLobby("Anna", makeView("v3", "Anna"));
        serverState.createNewGame("Anna", 2, Color.PURPLE, "v3");

        assertNotNull(cv1.lastLobby, "cv1 deve aver ricevuto il broadcast");
        assertNotNull(cv2.lastLobby, "cv2 deve aver ricevuto il broadcast");
    }

    @Test
    void broadcastLobbyContainsCorrectGameCount() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        // Crea 3 partite
        for (int i = 0; i < 3; i++) {
            String nick = "Player" + i;
            String vid  = "v" + i;
            serverState.getLobby(nick, makeView(vid, nick));
            serverState.createNewGame(nick, 2, Color.RED, vid);
        }

        assertNotNull(observer.lastLobby);
        assertEquals(3, observer.lastLobby.size());
    }

    @Test
    void broadcastError() {
        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);

        // Conta quante volte sendLobby viene chiamata:
        // la prima volta (addViewer) va bene, dalla seconda in poi lancia eccezione
        final int[] sendLobbyCount = {0};
        VirtualView faultyView = new VirtualView() {
            @Override public String getId()       { return "v1"; }
            @Override public String getNickname() { return "Alberto"; }
            @Override public void sendGame(GameDTO g) {}
            @Override public void sendClientState(ClientState cs) {}
            @Override public void sendLobby(List<LobbyInfoDTO> l) {
                sendLobbyCount[0]++;
                if (sendLobbyCount[0] > 1) {
                    throw new RuntimeException("Connessione interrotta");
                }
            }
            @Override public void showMessage(String m) {}
            @Override public void showLoginError(String m) {}
            @Override public void showActionRejected(String r) {}
            @Override public void showActionAccepted(String m) {}
            @Override public void showLeaderboard(List<GameResult> l, int p) {}
        };

        serverState.getLobby("Alberto", faultyView);

        // Forziamo un broadcast — faultyView lancia eccezione e viene rimossa
        assertDoesNotThrow(() -> {
            serverState.getLobby("Mattia", makeView("v2", "Mattia"));
            serverState.createNewGame("Mattia", 2, Color.PURPLE, "v2");
        });

        // Il broadcast deve continuare normalmente per gli altri viewer
        assertNotNull(observer.lastLobby);
    }

    // =========================================================================
    // restoreGame
    // =========================================================================

    @Test
    void restoreGameDoesNotThrow() {
        // Senza file su disco deve completare senza eccezioni
        assertDoesNotThrow(() -> serverState.initializeFromDisk());
    }

}
