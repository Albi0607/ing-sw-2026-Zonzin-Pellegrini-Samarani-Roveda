package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.RMI.CallBackImplementation;
import it.polimi.ingsw.mesos.RMI.RMIVirtualView;
import it.polimi.ingsw.mesos.RMI.client_RMI;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.rete.View;
import it.polimi.ingsw.mesos.rete.VirtualView;
import it.polimi.ingsw.mesos.socket.SocketVirtualView;
import it.polimi.ingsw.mesos.socket.clientSocket;
import it.polimi.ingsw.mesos.view.CLI.CLI;
import it.polimi.ingsw.mesos.view.GUI.Core.GUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;

class ServerStateTest {
    //classi di supporto ai test:

    //virtualView fittizia da poter usare e passare come parametro
    private VirtualView makeView(String id, String nickname) {
        return new VirtualView() {
            @Override public String getId()       { return id; }
            @Override public String getNickname() { return nickname; }
            @Override public void sendGame(GameDTO g) {}
            @Override public void sendClientState(ClientState cs) {}
            @Override public void sendLobby(List<LobbyInfoDTO> l) {}
            @Override public void showMessage(String m) {}
            @Override public void showLoginError(String m) {}
            @Override public void showActionRejected(String r) {}
            @Override public void showActionAccepted(String m) {}
            @Override public void showLeaderboard(List<GameResult> l, int p) {}
        };
    }

    //virtualView che cattura tutti i messaggi che si possono ricevere
    //dal server
    private static class CapturingView implements VirtualView {
        final String id;
        final String nickname;

        String lastMessage;
        String lastLoginError;
        String lastRejected;
        String lastAccepted;
        List<LobbyInfoDTO> lastLobby;

        CapturingView(String id, String nickname) {
            this.id       = id;
            this.nickname = nickname;
        }

        @Override public String getId()       { return id; }
        @Override public String getNickname() { return nickname; }
        @Override public void sendGame(GameDTO g) {}
        @Override public void sendClientState(ClientState cs) {}
        @Override public void sendLobby(List<LobbyInfoDTO> l) { lastLobby = l; }
        @Override public void showMessage(String m)           { lastMessage = m; }
        @Override public void showLoginError(String m)        { lastLoginError = m; }
        @Override public void showActionRejected(String r)    { lastRejected = r; }
        @Override public void showActionAccepted(String m)    { lastAccepted = m; }
        @Override public void showLeaderboard(List<GameResult> l, int p) {}

        //Azzera tutti i campi catturati
        void reset() {
            lastMessage    = null;
            lastLoginError = null;
            lastRejected   = null;
            lastAccepted   = null;
            lastLobby      = null;
        }
    }

    //creazione del serverState per ogni test
    private ServerState serverState;

    @BeforeEach
    void setUp() {
        serverState = new ServerState();
    }

    // =========================================================================
    // getLobby
    // =========================================================================

    @Nested
    class GetLobbyTests {

        @Test
        void newPlayerNicknameRegistered() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));

            assertTrue(serverState.isNicknameTaken("Alberto"));
        }

        @Test
        void newPlayerConnectionRegistered() {
            VirtualView view = makeView("v1", "Alberto");
            serverState.getLobby("Alberto", view);

            assertSame(view, serverState.getConnection("v1"));
        }


        @Test
        void duplicateNickname() {
            CapturingView second = new CapturingView("v2", "Alberto");

            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.getLobby("Alberto", second);

            assertNotNull(second.lastLoginError);
            assertEquals("Nickname già in uso", second.lastLoginError);
        }

        @Test
        void duplicateNicknameSecondViewNotStoredInConnections() {
            CapturingView second = new CapturingView("v2", "Alberto");

            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.getLobby("Alberto", second);

            // v2 non deve essere stata aggiunta alle connessioni
            assertNull(serverState.getConnection("v2"));
        }

        @Test
        void duplicateNicknameOriginalConnectionPreserved() {
            VirtualView first = makeView("v1", "Alberto");
            serverState.getLobby("Alberto", first);
            serverState.getLobby("Alberto", makeView("v2", "Alberto"));

            assertSame(first, serverState.getConnection("v1"));
        }

        @Test
        void playerAlreadyInGame() {
            // Alberto entra in lobby, crea una partita da 2: è in playerToGame
            CapturingView aliceView = new CapturingView("v1", "Alberto");
            serverState.getLobby("Alberto", aliceView);
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            // Un secondo client prova a usare il nickname "Alberto" mentre è in gioco
            CapturingView intruder = new CapturingView("v_intruder", "Alberto");
            serverState.getLobby("Alberto", intruder);


            assertNotNull(intruder.lastLoginError);
        }

        @Test
        void multipleDistinctPlayers() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.getLobby("Anna",   makeView("v2", "Anna"));
            serverState.getLobby("Mattia", makeView("v3", "Mattia"));

            assertTrue(serverState.isNicknameTaken("Alberto"));
            assertTrue(serverState.isNicknameTaken("Anna"));
            assertTrue(serverState.isNicknameTaken("Mattia"));
        }
    }

    // =========================================================================
    // getConnection
    // =========================================================================

    @Nested
    class GetConnectionTests {

        @Test
        void returnsCorrectView() {
            VirtualView view = makeView("v1", "Alberto");
            serverState.getLobby("Alberto", view);

            assertSame(view, serverState.getConnection("v1"));
        }

        @Test
        void unknownId() {
            assertNull(serverState.getConnection("nonexistent"));
        }

        @Test
        void afterRemoveConnection() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.removeConnection("v1");

            assertNull(serverState.getConnection("v1"));
        }
    }

    // =========================================================================
    // createNewGame
    // =========================================================================

    @Nested
    class CreateNewGameTests {

        @Test
        void createNewGame() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            assertNotNull(serverState.getController("Alberto"));
        }

        @Test
        void createNewGameConfirmationMessage() {
            CapturingView cv = new CapturingView("v1", "Alberto");
            serverState.getLobby("Alberto", cv);
            cv.reset();

            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            assertNotNull(cv.lastMessage,
                    "Dopo createNewGame deve essere inviato un messaggio di conferma");
        }

        @Test
        void success_withMaxPlayers_controllerRegistered() {
            serverState.getLobby("Alice", makeView("v1", "Alice"));
            serverState.createNewGame("Alice", 5, Color.RED, "v1");

            assertNotNull(serverState.getController("Alice"),
                    "createNewGame con 5 giocatori (massimo) deve funzionare correttamente");
        }

        @Test
        void success_withMinPlayers_controllerRegistered() {
            serverState.getLobby("Alice", makeView("v1", "Alice"));
            serverState.createNewGame("Alice", 2, Color.RED, "v1");

            assertNotNull(serverState.getController("Alice"));
        }

        @Test
        void invalidNumPlayersTooFew() {
            CapturingView cv = new CapturingView("v1", "Alberto");
            serverState.getLobby("Alberto", cv);
            cv.reset();

            serverState.createNewGame("Alberto", 1, Color.RED, "v1");

            assertNotNull(cv.lastRejected);
            assertNull(serverState.getController("Alberto"));
        }

        @Test
        void invalidNumPlayersTooMany() {
            CapturingView cv = new CapturingView("v1", "Alberto");
            serverState.getLobby("Alberto", cv);
            cv.reset();

            serverState.createNewGame("Alberto", 6, Color.RED, "v1");

            assertNotNull(cv.lastRejected);
            assertNull(serverState.getController("Alberto"));
        }

        @Test
        void unknownVirtualViewId() {
            serverState.createNewGame("Ghost", 2, Color.RED, "nonexistent");

            assertNull(serverState.getController("Ghost"));
        }

        @Test
        void viewNotInLobbyViewers() {
            // Alberto crea una partita quindi viene rimossa dai viewer di lobby
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            // Tenta di creare una seconda partita: Alberto è ancora in connections
            // ma non più in lobby viewers quindi il check !lobby.containView deve bloccarla
            GameController firstController = serverState.getController("Alberto");

            serverState.createNewGame("Alberto", 3, Color.BLUE, "v1");

            assertSame(firstController, serverState.getController("Alberto"));
        }
    }

    // =========================================================================
    // joinGame
    // =========================================================================

    @Nested
    class JoinGameTests {

        @Test
        void joinGame() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            serverState.getLobby("Anna", makeView("v2", "Anna"));
            serverState.joinGame("Anna", 1, Color.BLUE, "v2");

            assertNotNull(serverState.getController("Anna"));
        }

        @Test
        void joinGameSameController() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            serverState.getLobby("Luca", makeView("v2", "Luca"));
            serverState.joinGame("Luca", 1, Color.BLUE, "v2");

            assertSame(
                    serverState.getController("Alberto"),
                    serverState.getController("Luca")
            );
        }

        @Test
        void nonExistentGame() {
            CapturingView cv = new CapturingView("v1", "Alberto");
            serverState.getLobby("Alberto", cv);
            cv.reset();

            serverState.joinGame("Alberto", 999, Color.RED, "v1");

            assertNotNull(cv.lastRejected);
        }

        @Test
        void nonExistentGameNoController() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.joinGame("Alberto", 999, Color.RED, "v1");

            assertNull(serverState.getController("Alberto"));
        }

        @Test
        void unknownVirtualViewId() {
            serverState.joinGame("Ghost", 1, Color.RED, "nonexistent");

            assertNull(serverState.getController("Ghost"));
        }


        @Test
        void threePlayersJoinSameGame_allShareSameController() {
            serverState.getLobby("Alberto",   makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 3, Color.RED, "v1");

            serverState.getLobby("Mattia",     makeView("v2", "Mattia"));
            serverState.joinGame("Mattia",   1, Color.BLUE,  "v2");

            serverState.getLobby("Luca", makeView("v3", "Luca"));
            serverState.joinGame("Luca", 1, Color.PURPLE, "v3");

            GameController AlbertoCtrl   = serverState.getController("Alberto");
            GameController MattiaCtrl     = serverState.getController("Mattia");
            GameController LucaCtrl = serverState.getController("Luca");

            assertNotNull(AlbertoCtrl);
            assertSame(AlbertoCtrl, MattiaCtrl);
            assertSame(AlbertoCtrl, LucaCtrl);
        }
    }

    // =========================================================================
    // removePlayer
    // =========================================================================

    @Nested
    class RemovePlayerTests {

        @Test
        void removePlayer() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.removePlayer("Alberto");

            assertFalse(serverState.isNicknameTaken("Alberto"));
        }

        @Test
        void controllerNoLongerAssociated() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            serverState.removePlayer("Alberto");

            assertNull(serverState.getController("Alberto"));
        }


        @Test
        void removedNicknameCanBeReusedinNewSession() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.removePlayer("Alberto");
            serverState.removeConnection("v1");

            // Una nuova connessione può usare lo stesso nickname
            VirtualView newView = makeView("v2", "Alberto");
            serverState.getLobby("Alberto", newView);

            assertTrue(serverState.isNicknameTaken("Alberto"));
            assertSame(newView, serverState.getConnection("v2"));
        }
    }

    // =========================================================================
    // removeConnection
    // =========================================================================

    @Nested
    class RemoveConnectionTests {

        @Test
        void connectionRemoved() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.removeConnection("v1");

            assertNull(serverState.getConnection("v1"));
        }

        @Test
        void removeNonExistentConnection() {
            assertDoesNotThrow(() -> serverState.removeConnection("nonexistent"));
        }

        @Test
        void afterRemoveConnectionNoBroadcast() {
            CapturingView Alberto = new CapturingView("v1", "Alberto");
            serverState.getLobby("Alberto", Alberto);
            serverState.removeConnection("v1");

            Alberto.reset();

            // Un altro giocatore crea una partita → broadcast
            serverState.getLobby("Anna", makeView("v2", "Anna"));
            serverState.createNewGame("Anna", 2, Color.BLUE, "v2");

            assertNull(Alberto.lastLobby);
        }

    }

    // =========================================================================
    // getController
    // =========================================================================

    @Nested
    class GetControllerTests {

        @Test
        void returnsNullBeforeAnyGame() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));

            assertNull(serverState.getController("Alberto"));
        }

        @Test
        void returnsControllerAfterCreateGame() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            assertNotNull(serverState.getController("Alberto"));
        }

        @Test
        void returnsControllerAfterJoinGame() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.createNewGame("Alberto", 2, Color.RED, "v1");

            serverState.getLobby("Luca", makeView("v2", "Luca"));
            serverState.joinGame("Luca", 1, Color.BLUE, "v2");

            assertNotNull(serverState.getController("Luca"));
        }

        @Test
        void returnsNullForUnknownNickname() {
            assertNull(serverState.getController("Nobody"));
        }
    }

    // =========================================================================
    // isNicknameTaken
    // =========================================================================

    @Nested
    class IsNicknameTakenTests {

        @Test
        void falseBeforeAnyRegistration() {
            assertFalse(serverState.isNicknameTaken("Alberto"));
        }

        @Test
        void trueAfterGetLobby() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));

            assertTrue(serverState.isNicknameTaken("Alberto"));
        }

        @Test
        void falseAfterRemovePlayer() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));
            serverState.removePlayer("Alberto");

            assertFalse(serverState.isNicknameTaken("Alberto"));
        }

        @Test
        void otherNicknamesUnaffected() {
            serverState.getLobby("Alberto", makeView("v1", "Alberto"));

            assertFalse(serverState.isNicknameTaken("Mattia"));
        }
    }

    @Test
    void initializeFromDisk_noFilesOnDisk_doesNotThrow() {
        assertDoesNotThrow(() -> serverState.initializeFromDisk());
    }

    @Test
    void initializeFromDiskNoFilesOnDisk() {

        File dir = new File(".");
        File[] logFiles = dir.listFiles((d, name) -> name.matches("mesos_game_\\d+\\.log"));
        if (logFiles != null) {
            for (File f : logFiles) f.delete();
        }


        CapturingView observer = new CapturingView("obs", "Observer");
        serverState.getLobby("Observer", observer);
        observer.reset();

        serverState.initializeFromDisk();

        // Forziamo un broadcast con createNewGame
        serverState.getLobby("Alberto", makeView("v1", "Alberto"));
        serverState.createNewGame("Alberto", 2, Color.RED, "v1");

        assertNotNull(observer.lastLobby);
        assertEquals(1, observer.lastLobby.size());
    }
}