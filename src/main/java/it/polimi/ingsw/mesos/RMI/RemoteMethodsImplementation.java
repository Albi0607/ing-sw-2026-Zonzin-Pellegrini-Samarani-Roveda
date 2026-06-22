package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.multipleGames.ServerState;
import it.polimi.ingsw.mesos.network.VirtualView;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Implementation of the RemoteMethods interface.
 *
 * This class represents the server-side entry point for all RMI client requests.
 * It receives remote invocations from clients and delegates the execution of game
 * and lobby actions to the ServerState and GameController components.
 * All game-related actions are executed asynchronously using an ExecutorService
 * to avoid blocking the RMI thread pool and to ensure better scalability.
 */
public class RemoteMethodsImplementation extends UnicastRemoteObject implements RemoteMethods {

    /**
     * Central server state containing all active games, players, and lobby data.
     */
    private final ServerState serverState;

    /**
     * Thread pool used to execute client requests asynchronously.
     */
    private final ExecutorService executor;

    // gestione keepAlive Message
    private final Map<String, Long> lastHeartbeat = new ConcurrentHashMap<>();

    /**
     * Creates a new RemoteMethodsImplementation instance
     *
     * @param serverState central server state managing games and lobby
     * @throws RemoteException if RMI export fails
     */
    public RemoteMethodsImplementation(ServerState serverState) throws RemoteException {
        this.serverState = serverState;
        this.executor = Executors.newCachedThreadPool();
        startWatchdog();
    }


    /**
     * Requests the server to place the player's totem on the OfferTile.
     * The request is executed asynchronously on the server thread pool.
     *
     * @param nickname name of the player performing the action
     * @param position selected position on the OfferTile
     * @throws RemoteException if a network error occurs during the remote call
     */
    @Override
    public void placeTotem(String nickname, char position) throws RemoteException {
        executor.submit(()-> {
            //forse c'è bisogno di controllare che il controller non sia null e/o fare alcuni controlli per notificare la view
            GameController controller = serverState.getController(nickname);
            if(controller==null){
                return;
            }
            controller.onPlaceTotem(nickname, position);
        });
    }

    /**
     * Requests the server to draw a card from the upper or lower row.
     * The request is executed asynchronously on the server thread pool.
     *
     * @param nickname name of the player performing the action
     * @param position index of the selected card
     * @param isUpper true if selecting from upper row, false otherwise
     * @throws RemoteException if a network error occurs during the remote call
     */
    @Override
    public void takeCard(String nickname, int position, boolean isUpper) throws RemoteException {
        executor.submit(()-> {
            GameController controller = serverState.getController(nickname);
            if(controller==null){
                return;
            }
            controller.onTakeCard(nickname, position, isUpper);
        });
    }

    /**
     * Requests to skip the extra card draw at the end of the turn.
     * The action is valid only if the player owns the corresponding building.
     * The request is executed asynchronously on the server thread pool.
     *
     * @param nickname name of the player performing the action
     * @throws RemoteException if a network error occurs during the remote call
     */
    @Override
    public void skipExtraDraw(String nickname) throws RemoteException{
        executor.submit(()-> {
            GameController controller = serverState.getController(nickname);
            if(controller==null){
                return;
            }
            controller.onSkipExtraDraw(nickname);
        });
    }

    /**
     * Registers a client in the lobby and returns its VirtualView identifier.
     *
     * @param nickname name of the connecting player
     * @param clientCallback remote callback used by the server to communicate with the client
     * @return unique identifier of the associated VirtualView
     * @throws RemoteException if a network error occurs during the remote call
     */
    public String getLobby(String nickname,CallBack clientCallback) throws RemoteException{
        try {
            String clientIP = RemoteServer.getClientHost();
            System.out.println("New RMI connection/request from: " + clientIP + " (Nickname: " + nickname + ")");
        } catch (ServerNotActiveException e) {
            System.out.println("RMI request da IP sconosciuto (Nickname: " + nickname + ")");
        }
        VirtualView view = new RMIVirtualView(nickname, clientCallback);
        serverState.getLobby(nickname,view);
        return view.getId();
    }

    /**
     * Requests the creation of a new game in the lobby.
     * The request is executed asynchronously.
     *
     * @param nickname name of the player creating the game
     * @param expectedNumPlayers number of players required for the game
     * @param color selected color for the new player
     * @param virtualViewId identifier of the client's VirtualView
     * @throws RemoteException if a network error occurs during the remote call
     */
    public void createNewGame(String nickname, int expectedNumPlayers, Color color, String virtualViewId) throws  RemoteException{
        executor.submit(()-> {
            //passo virtualViewId poiché utilizzo la stessa view creata in getLobby
            serverState.createNewGame(nickname, expectedNumPlayers, color, virtualViewId);
        });
    }

    /**
     * Requests to join an existing game in the lobby.
     * The request is executed asynchronously on the server thread pool.
     *
     * @param nickname name of the player joining the game
     * @param id identifier of the game
     * @param color selected color for the new player
     * @param virtualViewId identifier of the client's VirtualView
     * @throws RemoteException if a network error occurs during the remote call
     */
    public void joinGame(String nickname, int id, Color color, String virtualViewId) throws RemoteException{
        executor.submit(()-> {
            //passo virtualViewId poiché utilizzo la stessa view creata in getLobby
            serverState.joinGame(nickname, id, color, virtualViewId);
        });
    }

    /**
     * Records the current timestamp for the given player as proof of an active connection.
     *
     * <p>This method is called periodically by the client-side {@link KeepAliveRMI}
     * at a fixed interval. Each invocation updates the player's entry in
     * {@code lastHeartbeat} with the current system time, resetting the inactivity
     * countdown monitored by the watchdog thread started in {@link #startWatchdog()}.</p>
     *
     * @param nickname name of the player sending the heartbeat,
     *                 used as the key to update the timestamp map
     * @throws RemoteException if a network error occurs during the remote call
     */

    @Override
    public void heartbeat(String nickname) throws RemoteException {
        lastHeartbeat.put(nickname, System.currentTimeMillis());
    }

    /**
     * Starts a background watchdog thread that periodically checks all connected
     * players for heartbeat timeouts and handles disconnections accordingly.
     *
     * <p>The watchdog runs on a single-threaded scheduled executor and fires every
     * 5 seconds. On each tick it iterates over the {@code lastHeartbeat} map and
     * compares the current system time against each player's last recorded heartbeat.
     * If the elapsed time exceeds 50 seconds, the player is considered disconnected
     * and the following cleanup sequence is triggered:</p>
     * <ol>
     *   <li>The player's entry is removed from {@code lastHeartbeat}.</li>
     *   <li>A timeout message is printed to {@code stdout} for diagnostics.</li>
     *   <li>{@link GameController#onPlayerDisconnected(String)} is called on the
     *       player's active game controller (if present), so the game logic can
     *       react to the disconnection (e.g. pausing the game or reassigning the turn).</li>
     *   <li>{@link ServerState#removePlayer(String)} is called to clean up all
     *       remaining server-side references to the player.</li>
     * </ol>
     */
    private void startWatchdog() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            lastHeartbeat.forEach((nickname, lastTime) -> {
                if (now - lastTime > 50_000) {
                    lastHeartbeat.remove(nickname);
                    System.out.println("[RMI Watchdog] Timeout per: " + nickname);
                    var controller = serverState.getController(nickname);
                    if (controller != null) {
                        controller.onPlayerDisconnected(nickname);
                    }
                    serverState.removePlayer(nickname);
                }
            });
        }, 5, 5, TimeUnit.SECONDS);
    }

}
