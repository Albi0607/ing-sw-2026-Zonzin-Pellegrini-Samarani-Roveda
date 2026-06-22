package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.multipleGames.ServerState;
import it.polimi.ingsw.mesos.network.PlayerStatus;
import it.polimi.ingsw.mesos.socket.Message.Message;
import it.polimi.ingsw.mesos.socket.Message.messageClient.*;
import it.polimi.ingsw.mesos.socket.Message.messageServer.ErrorMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Handles a single client connection on the server side.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Managing input/output streams with the client</li>
 *     <li>Receiving and dispatching messages to the {@link GameController}</li>
 *     <li>Handling client registration</li>
 *     <li>Managing client-specific errors and disconnection</li>
 * </ul>
 * <p>
 * Each instance runs on its own thread and represents one connected client.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ServerState serverState;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String nickname;
    private String virtualViewId;
    private SocketVirtualView virtualView;

    // KeepAlive
    private static final long TIMEOUT_MS = 50_000; // 50 secondi
    private volatile long lastPingTime = System.currentTimeMillis();
    private volatile PlayerStatus status = PlayerStatus.CONNECTED;

    /**
     * Creates a new handler for a connected client.
     *
     * @param clientSocket the socket associated with the connected client
     * @param serverState the server-side list of lobbies
     */
    public ClientHandler(Socket clientSocket, ServerState serverState) {
        this.clientSocket = clientSocket;
        this.serverState = serverState;
    }

    /**
     * Starts the client communication loop.
     * <p>
     * Initializes I/O streams and continuously listens for incoming messages.
     */
    @Override
    public void run() {
        try {
            setupStreams();
            clientLoop();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /**
     * Initializes object input and output streams for communication.
     *
     * @throws IOException if stream initialization fails
     */
    private void setupStreams() throws IOException {
        clientSocket.setSoTimeout((int) TIMEOUT_MS);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(clientSocket.getInputStream());
    }


    /**
     * Continuously reads messages from the client while the connection is active.
     * <p>
     * The first valid message must always be a {@link RegisterMessage}.
     * Any other message before registration is ignored.
     */
    private void clientLoop() {
        try {
            while (true) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }
        } catch (SocketTimeoutException e) {
            // Nessun messaggio (neanche PING) entro 15s
            System.out.println("Timeout keepalive: " + nickname);
            handleDisconnection();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnesso: " + nickname);
            handleDisconnection();
        }
    }

    /**
     * Marks the player as disconnected and notifies the associated {@link GameController}.
     *
     * <p>If the player was part of an active game, the controller is informed via
     * {@link GameController#onPlayerDisconnected(String)}. Repeated calls are ignored
     * to avoid duplicate disconnection handling.
     */
    private void handleDisconnection() {
        if (status == PlayerStatus.DISCONNECTED) return; // evita doppia chiamata
        status = PlayerStatus.DISCONNECTED;

        if (nickname == null) return;

        // Notifica il controller se il giocatore era in partita
        var controller = serverState.getController(nickname);
        if (controller != null) {
            controller.onPlayerDisconnected(nickname);
        }
    }

    /**
     * Handles an incoming message from the client.
     * <p>
     * - If the message is a {@link RegisterMessage}, it triggers registration.
     * - If the client is not registered, other messages are ignored.
     * - Otherwise, the message is executed on the server side through the controller.
     * <p>
     * If a game-related exception occurs, an error message is sent only to this client.
     *
     * @param message the received message from the client
     * @throws IOException if sending responses to the client fails
     */
    private void handleMessage(Message message) throws IOException {

        // messagio per KeepAlive
        if (message instanceof PingMessage) {
            return;
        }

        // Registrazione alla lobby (primo messaggio obbligatorio)
        if (message instanceof GetLobbyMessage glm) {
            handleGetLobby(glm);
            return;
        }

        if (nickname == null || virtualView == null) {
            System.err.println("Messaggio ricevuto prima della registrazione, ignorato.");
            return;
        }

        // Azioni lobby
        if (message instanceof CreateGameMessage cgm) {
            serverState.createNewGame(nickname, cgm.getNumPlayers(), cgm.getColor(), virtualViewId);
            return;
        }

        if (message instanceof JoinGameMessage jgm) {
            serverState.joinGame(nickname, jgm.getGameId(), jgm.getColor(),virtualViewId);
            return;
        }

        // Azioni di gioco, direziona al giusto controller
        try {
            // Cerca il controller associato a questo giocatore
            // (viene associato quando entra in una partita)
            var controller = serverState.getController(nickname);
            if (controller == null) {
                virtualView.showMessage("Non sei in nessuna partita.");
                return;
            }
            message.executeServerSide(controller);
        } catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
            virtualView.showMessage(e.getMessage());
        }
    }

    /**
     * Handles the initial lobby registration for a connecting client.
     *
     * <p>Assigns the player's nickname, creates a {@link SocketVirtualView} for them,
     * and delegates lobby registration to {@link ServerState}. If registration fails,
     * an {@link ErrorMessage} is sent to the client and the socket is closed.
     *
     * @param msg the {@link GetLobbyMessage} containing the player's chosen nickname
     * @throws IOException if sending the error response or closing the socket fails
     */
    private void handleGetLobby(GetLobbyMessage msg) throws IOException {
        try {
            this.nickname    = msg.getNickname();
            this.virtualView = new SocketVirtualView(nickname, out);
            this.virtualViewId = virtualView.getId();

            // Delega a ServerState
            serverState.getLobby(nickname, virtualView);

            //System.out.println("Registrato in lobby: " + nickname);
        } catch (Exception e) {
            System.err.println("Errore registrazione lobby '" + msg.getNickname() + "': " + e.getMessage());
            try {
                out.writeObject(new ErrorMessage(e.getMessage()));
                out.flush();
            } catch (IOException ignored) {}
            clientSocket.close();
        }
    }

    /**
     * Releases all resources associated with this client connection.
     *
     * <p>Always called when the client disconnects, whether due to an error or a
     * normal closure. Specifically:
     * <ul>
     *   <li>If the player was part of an active game, their slot is preserved to
     *       allow reconnection; disconnection is already handled by
     *       {@link #handleDisconnection()}.</li>
     *   <li>If the player had not yet joined any game, they are removed from
     *       {@link ServerState} entirely.</li>
     * </ul>
     * In both cases, the virtual view connection is unregistered and the socket
     * is closed.
     */
    private void cleanup() {
        System.out.println("[ClientHandler] Cleanup per: " + nickname);
        var controller = serverState.getController(nickname);

        if (controller != null) {
            // Era in partita → NON rimuovere, permetti riconnessione
            // la disconnessione è già gestita da handleDisconnection()
        } else {
            // Non era in partita → rimuovi normalmente
            // lo elimino perchè non è mai stato parte di una partita
            serverState.removePlayer(nickname);
        }

        // Rimuove la connessione e il viewer dalla lobby
        if (virtualViewId != null) {
            serverState.removeConnection(virtualViewId);
        }
        // Chiude il socket
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException ignored) {}
    }


}
