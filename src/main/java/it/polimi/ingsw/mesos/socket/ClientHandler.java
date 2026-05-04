package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;
import it.polimi.ingsw.mesos.socket.Message.messageClient.RegisterMessage;
import it.polimi.ingsw.mesos.socket.Message.messageServer.ErrorMessage;

import java.io.*;
import java.net.Socket;

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
    private final GameController controller;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String nickname;

    private SocketVirtualView virtualView;

    /**
     * Creates a new handler for a connected client.
     *
     * @param clientSocket the socket associated with the connected client
     * @param controller the server-side game controller
     */
    public ClientHandler(Socket clientSocket, GameController controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
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
        }
    }

    /**
     * Initializes object input and output streams for communication.
     *
     * @throws IOException if stream initialization fails
     */
    private void setupStreams() throws IOException {
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnesso: " + nickname);
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
        if (message instanceof RegisterMessage reg) {
            handleRegister(reg);
            return;
        }

        if (nickname == null) {
            // Client non ancora registrato → ignora qualsiasi altro messaggio
            System.err.println("Messaggio ricevuto prima della registrazione, ignorato.");
            return;
        }

        try {
            message.executeServerSide(controller);
        } catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
            // Errore di logica di gioco → risponde solo a questo client, non broadcast
            if (virtualView != null) {
                virtualView.showMessage(e.getMessage());
            }
        }
    }

    /**
     * Handles client registration by creating a {@link SocketVirtualView}
     * and registering the player in the {@link GameController}.
     * <p>
     * If registration fails (e.g., nickname already taken), an error message
     * is sent to the client and the connection is closed.
     *
     * @param reg the registration message containing the player's nickname
     * @throws IOException if communication with the client fails or socket closure fails
     */
    private void handleRegister(RegisterMessage reg) throws IOException {
        try {
            this.nickname    = reg.getNickname();
            this.virtualView = new SocketVirtualView(nickname, out);
            controller.addPlayer(nickname, virtualView);
            System.out.println( "Registrato: " + nickname);
        } catch (Exception e) {
            System.err.println("Errore registrazione '"
                    + reg.getNickname() + "': " + e.getMessage());
            // Manda errore e chiude — il client dovrà riconnettersi
            try {
                out.writeObject(new ErrorMessage(e.getMessage()));
                out.flush();
            } catch (IOException ignored) {}
            clientSocket.close(); // motivo del throws nella firma, potrebbe generare un'ecc inaspettata
        }
    }


}
