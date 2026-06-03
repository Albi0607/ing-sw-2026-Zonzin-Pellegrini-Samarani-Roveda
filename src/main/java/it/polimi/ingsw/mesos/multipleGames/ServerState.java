package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.persistence.GameMove;
import it.polimi.ingsw.mesos.persistence.GameRestorer;
import it.polimi.ingsw.mesos.persistence.MoveLogger;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General class responsible for managing the server state.
 *
 * It keeps track of all players currently connected to the server, whether they are:
 * - connected and in the lobby, waiting to create or join a game,
 * - or already assigned to an active game via a GameController.
 *
 * It also maintains the list of all connected player nicknames in order to prevent duplicates.
 *
 * This class is responsible for handling interactions with the lobby,
 * including game creation and game joining operations.
 *
 * It acts as a central registry for routing player requests to the correct GameController.
 */

public class ServerState {

    /**
     * Lobby instance responsible for managing game creation and joining logic.
     */
    private final Lobby lobby;
    /**
     * Map that associates a unique VirtualView identifier with its corresponding connection.
     * Used to communicate with connected clients.
     */
    private final Map<String, VirtualView> connections;
    /**
     * Maps each player nickname to the GameController managing their current game session.
     * Allows routing of player actions to the correct game instance.
     */
    private final Map<String, GameController> playerToGame;
    /**
     * Set of all currently connected player nicknames.
     * Ensures nickname uniqueness across the server.
     */
    private final Set<String> nicknames;
    /**
     * Map of players that were previously in a game but disconnected,
     * used to support reconnection to ongoing games.
     */
    private final Map<String, Integer> pendingReconnect;

    /**
     * Constructs a new ServerState instance and initializes all internal data structures.
     *
     * The constructor creates:
     * - a new Lobby instance responsible for managing game creation and joining,
     * - a thread-safe map for active client connections,
     * - a thread-safe mapping between player nicknames and their associated GameController,
     * - a thread-safe set of connected player nicknames to ensure uniqueness,
     * - and a map used to track players that are temporarily disconnected and eligible for reconnection.
     *
     * All collections are initialized using concurrent implementations to ensure
     * thread safety in a multi-client network environment.
     */
    public ServerState(){
        this.lobby = new Lobby(this);
        this.connections = new ConcurrentHashMap<>();
        this.playerToGame = new ConcurrentHashMap<>();
        this.nicknames  = ConcurrentHashMap.newKeySet();
        this.pendingReconnect = new ConcurrentHashMap<>();
    }

    /**
     * Handles player login and routing to the appropriate server state.
     *
     * The method manages three main cases:
     * - reconnection of a previously disconnected player,
     * - restoration of a pending game session,
     * - or new login into the lobby.
     *
     * It ensures nickname uniqueness and updates the internal mappings
     * between VirtualView connections, player nicknames, and GameController instances.
     * @param nickname the unique identifier of the player attempting to connect
     * @param view the VirtualView instance representing the client connection
     */
    public synchronized void getLobby(String nickname,VirtualView view){
        GameController controllerR = getController(nickname);
        if (controllerR != null) {
            // Il giocatore era in una partita → riconnessione
            if (controllerR.isPlayerDisconnected(nickname)) {
                // Sì, era crashato. Lo facciamo rientrare.
                String virtualViewId = view.getId();
                connections.put(virtualViewId, view);
                controllerR.reconnectPlayer(nickname, view);
            } else {
                // non è crashato, qualcun altro sta provando a usare il suo nickname mentre gioca.
                view.showLoginError("Il giocatore '" + nickname + "' è già online e sta giocando!");
            }
            return;
        }

        //gestione riconnessione
        if (pendingReconnect.containsKey(nickname)) {
            int gameId = pendingReconnect.remove(nickname);
            String virtualViewId = view.getId();
            connections.put(virtualViewId, view);
            nicknames.add(nickname);

            try{

                GameController controller = lobby.joinGame(gameId, nickname, null, virtualViewId);
                if (controller != null) {
                    playerToGame.put(nickname, controller);
                    System.out.println("[ServerState] Riconnessione di '" + nickname
                            + "' alla partita " + gameId);
                } else {
                    view.showMessage("Errore durante la riconnessione alla partita " + gameId);
                }
            }catch (Exception e){
                view.showMessage("Errore durante la riconnessione alla partita " + gameId + ": " + e.getMessage());
            }
            return;
        }

        if(isNicknameTaken(nickname)){
            view.showLoginError("Nickname già in uso");
            return;
        }

        String virtualViewId= view.getId();
        connections.put(virtualViewId,view);
        lobby.addViewer(virtualViewId);
        nicknames.add(nickname);
    }

    /**
     * Creates a new game for the specified player.
     * The method performs validation of the input parameters if the parameters are valid, it delegates the creation of
     * the game to the Lobby, which is responsible for instantiating the new game session.
     *
     * Appropriate feedback is sent to the client in case of success or failure
     *
     * @param nickname the nickname of the player creating the game
     * @param expectedNumPlayers the desired number of players for the game (must be between 2 and 5)
     * @param color the selected player color
     * @param virtualViewId the identifier of the client connection
     */

    public synchronized void createNewGame(String nickname, int expectedNumPlayers, Color color, String virtualViewId){

        VirtualView view = connections.get(virtualViewId);
        if(view==null || !lobby.containView(virtualViewId)){
            return;
        }

        try{
            if(expectedNumPlayers<2 || expectedNumPlayers>5){
                // Usiamo showActionRejected invece di showMessage
                view.showActionRejected("Numero di giocatori non corretto");
                return;
            }

            GameController controller = lobby.createNewGame(nickname, expectedNumPlayers, color, virtualViewId);
            if(controller == null){
                view.showActionRejected("GameController non creato correttamente");
                return;
            }

            playerToGame.put(nickname, controller);

            view.showMessage("Partita creata correttamente");

        } catch (Exception e) {
            view.showActionRejected(e.getMessage());
        }
    }

    /**
     * Adds a player to an existing game.
     *
     * The method validates the client connection and delegates the join operation
     * to the Lobby, which handles the assignment of the player to the specified game.
     *
     * If the operation succeeds, the player is associated with the corresponding GameController.
     * In case of errors, appropriate feedback is sent to the client.
     *
     * @param nickname the nickname of the player joining the game
     * @param id the identifier of the target game session
     * @param color the selected player color
     * @param virtualViewId the identifier of the client connection
     */
    public synchronized void joinGame(String nickname, int id, Color color, String virtualViewId){
        VirtualView view = connections.get(virtualViewId);
        if(view==null || !lobby.containView(virtualViewId)){
            return;
        }

        try {
            GameController controller = lobby.joinGame(id, nickname, color, virtualViewId);
            playerToGame.put(nickname, controller);

        } catch (Exception e) {
            view.showActionRejected(e.getMessage());
        }
    }


    /**
     * Initializes the server state by scanning the local filesystem for interrupted game sessions.
     *
     * This method searches for log files matching the pattern "mesos_game_{id}.log" and
     * reconstructs the corresponding game sessions, making them available for restoration.
     *
     * For each valid log file, the method:
     * - extracts the original game ID,
     * - retrieves the list of players involved in the game,
     * - recreates the corresponding GameController,
     * - and registers a GameRestorer to enable state recovery.
     *
     * Players involved in these sessions are marked as pending reconnection,
     * ensuring that only the original participants can resume the game.
     *
     * Restored games are added to the Lobby and marked as already started,
     * allowing players to reconnect and continue from the previous state.
     *
     * This method is executed once at server startup before initializing RMI and Socket services.
     */
    public synchronized void initializeFromDisk() {
        File dir = new File(".");
        File[] logFiles = dir.listFiles(
                (d, name) -> name.matches("mesos_game_\\d+\\.log")
        );

        if (logFiles == null || logFiles.length == 0) {
            System.out.println("[ServerState] Nessuna partita interrotta trovata su disco.");
            return;
        }

        Pattern pattern = Pattern.compile("mesos_game_(\\d+)\\.log");

        for (File logFile : logFiles) {
            Matcher matcher = pattern.matcher(logFile.getName());
            if (!matcher.matches()) continue;

            int gameId = Integer.parseInt(matcher.group(1));
            MoveLogger logger = new MoveLogger(logFile.getName());

            if (!logger.hasSavedGame()) continue;

            // Ricava i nickname che partecipavano alla partita dal log
            // (sono le mosse ADD_PLAYER)
            List<String> originalNicknames = logger.readAll().stream()
                    .filter(m -> m.type == GameMove.MoveType.ADD_PLAYER)
                    .map(m -> m.nickname)
                    .toList();

            if (originalNicknames.isEmpty()) continue;

            // Crea un GameController con lo stesso ID (usa lo stesso log file)
            GameController controller = new GameController(gameId);

            // Registra il restorer nel controller —
            // partirà non appena tutti i nickname originali si riconnetteranno
            GameRestorer restorer = new GameRestorer(logger);
            controller.setRestorer(restorer);

            // Registra i nickname come "attesi" così non possono essere usati
            // da altri giocatori di altre partite
            for (String nick : originalNicknames) {
                pendingReconnect.put(nick, gameId);
            }

            // Aggiunge la partita alla lobby con l'id originale
            // (così il nextId della Lobby non sovrascrive un ID già usato)
            lobby.restoreGame(gameId, controller);

            System.out.println("[ServerState] Partita " + gameId +
                    " ripristinabile. Giocatori attesi: " + originalNicknames);
        }
    }

    //metodi di supporto e che rendono il serverState coerente in caso di disconnessioni o fine partita

    /**
     * Removes a player from the server state.
     * The method deletes the player's nickname from the global registry
     * and removes any association with an active GameController.
     *
     * @param nickname the nickname of the player to remove
     */
    public synchronized void removePlayer(String nickname) {
        nicknames.remove(nickname);
        playerToGame.remove(nickname);
    }

    /**
     * Removes a client connection from the server.
     * The method removes the VirtualView associated with the given identifier
     * and unregisters it from the lobby.
     *
     * @param virtualViewId the identifier of the client connection to remove
     */

    //per rimuovere connessione a tutto
    public void removeConnection(String virtualViewId){
        connections.remove(virtualViewId);
        lobby.removeViewer(virtualViewId);
    }

    /**
     * Retrieves the VirtualView associated with the given connection identifier.
     *
     * @param virtualViewId the identifier of the client connection
     * @return the corresponding VirtualView, or null if not found
     */
    public VirtualView getConnection(String virtualViewId){
        return connections.get(virtualViewId);
    }


    /**
     * Retrieves the GameController associated with the specified player.
     *
     * @param nickname the nickname of the player
     * @return the GameController managing the player's game session, or null if not assigned
     */
    public GameController getController(String nickname){
        return playerToGame.get(nickname);
    }

    /**
     * Checks whether a nickname is already in use by a connected player.
     *
     * @param nickname the nickname to check
     * @return true if the nickname is already taken, false otherwise
     */
    public synchronized boolean isNicknameTaken(String nickname){
        return nicknames.contains(nickname);
    }

    //gestire l'aggiornamento di questa classe da parte della rete dopo che l'interazione con la lobby è positiva

}
