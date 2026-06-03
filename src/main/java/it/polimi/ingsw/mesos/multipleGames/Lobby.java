package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Lobby is responsible for managing and presenting all game sessions that are
 * currently waiting for players.
 *
 * It represents the main entry point after a successful login, allowing players
 * to either create a new game or join an existing one in order to start playing.
 *
 * The Lobby also includes games that were previously interrupted due to a crash
 * and successfully restored from disk, making them available again for completion.
 */
public class Lobby {

    /**
     * Reference to the global server state, used to access active connections
     * and coordinate interactions between network and game logic layers.
     */
    private final ServerState serverState;
    /**
     * Map of active game sessions indexed by their unique identifier.
     * Each entry represents a running and joinable game managed by a GameController.
     */
    private final Map<Integer, GameController> games = new ConcurrentHashMap<>();
    /**
     * Counter used to generate unique sequential identifiers for new game sessions.
     */
    private int nextId;
    /**
     * Set of active lobby viewers (clients currently observing the lobby state).
     * Used to broadcast updates about available games and their status.
     */
    private final Set<String> viewers = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a new Lobby instance.
     *
     * The Lobby is automatically created when the global ServerState is initialized,
     * as it is an attribute of it.
     *
     * @param serverState the global server state that provides access to active connections
     * and server-wide data structures
     */
    public Lobby(ServerState serverState) {
        this.serverState = serverState;
        nextId = 1;
    }

    /**
     * Adds a client to the list of lobby viewers and sends the current lobby state.
     *
     * The method retrieves the corresponding VirtualView from the ServerState.
     * If the connection is not valid, the viewer is removed.
     * Otherwise, the client is registered as a lobby viewer and immediately receives
     * an updated snapshot of all available games.
     *
     * @param virtualViewId the identifier of the client connection to register as a viewer
     */
    public synchronized void addViewer(String virtualViewId) {
        VirtualView view = serverState.getConnection(virtualViewId);
        if (view == null) {
            removeViewer(virtualViewId);
            return;
        }
        viewers.add(virtualViewId);
        view.sendLobby(buildLobby());
    }

    /**
     * Removes a client from the lobby viewers list.
     *
     * This is typically done when the player joins a game and no longer needs
     * real-time updates about available lobby sessions.
     *
     * @param virtualViewId the identifier of the client connection to remove
     */
    public synchronized void removeViewer(String virtualViewId) {
        viewers.remove(virtualViewId);
    }


    /**
     * Creates a new game session and registers it in the lobby.
     *
     * The method retrieves the client connection and, if valid, creates a new GameController
     * with a unique identifier. The game is initialized with the expected number of players
     * and the creator is immediately added as the first participant.
     *
     * The created game is registered in the active games list and associated callbacks are
     * configured to handle game termination and cleanup.
     *
     * After creation, the client is removed from the lobby viewers and all connected clients
     * are notified through a lobby broadcast update.
     *
     * @param nickname the nickname of the player creating the game
     * @param expectedNumPlayers the number of players required to start the game
     * @param color the color chosen by the player
     * @param virtualViewId the identifier of the client connection
     * @return the created GameController instance, or null if the connection is invalid
     * @throws Exception if an error occurs during game creation or initialization
     */
    public synchronized GameController createNewGame(String nickname, int expectedNumPlayers, Color color, String virtualViewId) throws Exception{
        //controlli per gestione di eventuali errori e cambiare client.State al client

        //try {
            VirtualView view = serverState.getConnection(virtualViewId);
            if (view == null) {
                return null;
            }
            int id = nextId++;
            GameController controller = new GameController(id);
            controller.setOnGameFinished(() -> removeFinishedGame(id));
            controller.setNumPlayers(expectedNumPlayers);
            games.put(id, controller);
            System.out.println("Nuovo game creato con questo id: " + id);
            games.get(id).addPlayer(nickname, color, view);
            removeViewer(virtualViewId);
            broadcast();
            return controller;
        /*} catch (Exception e) {
            System.out.println("Creazione del gioco non riuscita");
            return null;
        }
         */
    }

    /**
     * Adds a player to an existing game session.
     *
     * The method retrieves the target game and validates its current state.
     * A player can join only if the game exists and is either not started yet
     * or is in a valid restoration state.
     *
     * If the game has already started and is not recoverable, the join request is rejected.
     *
     * Upon successful validation, the player is added to the game, removed from the lobby
     * viewers list, and all clients are notified through a lobby broadcast update.
     *
     * @param id the identifier of the target game session
     * @param nickname the nickname of the player joining the game
     * @param color the selected player color
     * @param virtualViewId the identifier of the client connection
     * @return the GameController managing the target game session
     * @throws IllegalArgumentException if the game with the given id does not exist
     * @throws IllegalStateException if the game has already started and cannot accept new players
     * @throws Exception if an error occurs during player addition or game processing
     */
    public synchronized GameController joinGame(int id, String nickname, Color color, String virtualViewId) throws Exception{
        VirtualView view = serverState.getConnection(virtualViewId);
        if (view == null) {
            return null;
        }
        GameController controller = games.get(id);
        if (controller == null) {
            throw new IllegalArgumentException("Partita non trovata");
        }
        if (controller.getGame() != null && !controller.hasRestorer()) {
            throw new IllegalStateException("La partita è già piena o iniziata");
        }
        // Partita non ancora iniziata OPPURE in attesa di ripristino
        //try {
            controller.addPlayer(nickname, color, view);
            removeViewer(virtualViewId);
            broadcast();
            return controller;
        /*} catch (Exception e) {
            System.out.println("Errore generico che non permette di entrare nella partita con id: " + id + ": " + e.getMessage());
            return null;
        }

         */
    }

    /**
     * Removes inactive or orphaned game sessions from the lobby.
     *
     * A game is considered removable if it has not started and has no connected players.
     * These "zombie" games typically occur when all clients disconnect before the game begins.
     *
     * The method does not remove:
     * - games that are already started,
     * - games that are in a restoration state (i.e., managed by a restorer),
     * since both must remain available in the lobby for continuation or recovery.
     */
    public synchronized void removeEmptyGames() {
        games.entrySet().removeIf(entry -> {
            GameController controller = entry.getValue();

            // Non tocca partite in attesa di ripristino
            if (controller.hasRestorer()) return false;

            // Non tocca partite già avviate
            if (controller.getGame() != null) return false;

            // Rimuovi se non ci sono giocatori connessi
            boolean isEmpty = controller.getNumPlayersConnected() == 0;
            if (isEmpty) {
                System.out.println("[Lobby] Partita " + entry.getKey()
                        + " rimossa: nessun giocatore connesso.");
            }
            return isEmpty;
        });
    }

    /**
     * Removes a finished game session from the lobby.
     *
     * This method is invoked when a game ends normally via GameController.
     * It performs cleanup operations by removing the game from the active sessions
     * and clearing all associated player data from the ServerState.
     * This allows players to reuse their nicknames in future game sessions.
     * After removal, all lobby viewers are notified through a broadcast update.
     *
     * @param id the identifier of the finished game session to remove
     */
    public synchronized void removeFinishedGame(int id) {
        GameController controller = games.get(id);
        if (controller == null) return;

        // Rimuovi i nickname dei giocatori da ServerState
        // così possono riconnettersi con lo stesso nome in una nuova partita
        if (controller.getGame() != null) {
            controller.getGame().getPlayers()
                    .forEach(p -> serverState.removePlayer(p.getNickname()));
        }

        games.remove(id);
        broadcast();
        System.out.println("[Lobby] Partita " + id + " terminata e rimossa dalla lobby.");
    }

    /**
     * Broadcasts the current lobby state to all registered viewers.
     *
     * The method first performs a cleanup of empty game sessions, then builds an
     * updated snapshot of the lobby state.
     *
     * It iterates over all registered viewers and sends them the updated lobby information.
     * If a client connection is no longer valid or an error occurs during transmission,
     * the viewer is removed from the lobby.
     *
     * This ensures that all connected clients maintain a consistent and up-to-date view
     * of available game sessions.
     */
    public synchronized void broadcast(){
        removeEmptyGames();
        List<LobbyInfoDTO> lobby = buildLobby();

        Iterator<String> iterator = viewers.iterator();
        while(iterator.hasNext()){
            String virtualViewId = iterator.next();
            VirtualView view = serverState.getConnection(virtualViewId);
            if(view==null){
                removeViewer(virtualViewId);
                continue;
            }
            try{
                view.sendLobby(lobby);
            } catch (Exception e) {
                removeViewer(virtualViewId);
            }
        }
    }

    /**
     * Builds a snapshot representation of the current lobby state.
     *
     * The method converts all active game sessions into a list of LobbyInfoDTO objects,
     * which contain essential information required by clients to display available games.
     * Each DTO includes the game identifier, current number of players, maximum number
     * of players, whether the game has started, and the list of already selected colors.
     *
     * @return a list of LobbyInfoDTO representing the current state of all active games
     */
    private List<LobbyInfoDTO> buildLobby(){

        List<LobbyInfoDTO> lobbyGames = new ArrayList<>();
        for(Map.Entry<Integer, GameController> game: games.entrySet()){
            GameController controller = game.getValue();
            LobbyInfoDTO dto = new LobbyInfoDTO();

            dto.id = game.getKey();
            dto.numPlayers=controller.getNumPlayersConnected();
            dto.maxNumPlayers=controller.getExpectedNumPlayers();
            dto.started=(controller.getGame()!=null);
            dto.takenColors = controller.getTakenColors();
            lobbyGames.add(dto);
        }
        return lobbyGames;
    }

    /**
     * Restores a previously persisted game session into the lobby.
     *
     * This method is invoked during server initialization to reintroduce games
     * that were recovered from disk after an unexpected shutdown.
     * The restored game is re-registered in the active games list and configured
     * with its completion callback. The internal game ID generator is also updated
     * to prevent identifier collisions with existing or future games.
     *
     * After restoration, the lobby is updated and all clients are notified through a broadcast.
     *
     * @param gameId the original identifier of the restored game session
     * @param controller the GameController associated with the restored game
     */
    public synchronized void restoreGame(int gameId, GameController controller) {
        games.put(gameId, controller);
        controller.setOnGameFinished(() -> removeFinishedGame(gameId));

        // Garantisce che i prossimi ID siano sempre maggiori di quelli già esistenti
        if (gameId >= nextId) {
            nextId = gameId + 1;
        }

        System.out.println("[Lobby] Partita " + gameId + " reinserita in lobby per il ripristino.");
        broadcast();
    }

    /**
     * Checks whether a client is currently registered as a lobby viewer.
     *
     * @param virtualViewId the identifier of the client connection
     * @return true if the client is present in the lobby viewers list, false otherwise
     */
    public boolean containView(String virtualViewId){
        return viewers.contains(virtualViewId);
    }

}
