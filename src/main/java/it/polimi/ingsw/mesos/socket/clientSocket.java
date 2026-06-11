package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.*;
import it.polimi.ingsw.mesos.socket.Message.messageClient.*;

import java.io.*;
import java.net.Socket;

/**
 * Implementation of the {@link Network} interface using Java Socket communication.
 * <p>
 * This class is responsible for establishing a connection with the server,
 * sending serialized {@link Message} objects, and continuously listening
 * for incoming messages from the server on a dedicated thread.
 * <p>
 * All server communication is performed using {@link ObjectOutputStream}
 * and {@link ObjectInputStream}.
 */

public class clientSocket implements Network {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ClientController controller;
    private KeepAliveSender keepAlive;

    /**
     * Creates a new client socket and connects to the specified server.
     * <p>
     * Initializes input and output streams and starts a background thread
     * to continuously listen for messages from the server.
     *
     * @param host the server hostname or IP address
     * @param port the server port number
     * @throws IOException if an I/O error occurs when creating the socket or streams
     */
    public clientSocket(String host, int port) throws IOException {
        socket = new Socket(host, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        keepAlive = new KeepAliveSender(out);
        Thread keepAliveThread = new Thread(keepAlive);
        keepAliveThread.setDaemon(true);
        keepAliveThread.start();

        new Thread(this::listenFromServer).start();
    }

    /**-+
     * Sends a request to place a totem in the game.
     *
     * @param nickname the nickname of the player
     * @param position the position where the totem should be placed
     * @return always returns {@code true} (message is sent asynchronously)
     */
    @Override
    public boolean placeTotem(String nickname, char position) {
        return sendMessage(new PlaceTotemMessage(nickname, position));
    }

    /**
     * Sends a request to take a card from a specific position.
     *
     * @param nickname the nickname of the player
     * @param position the position of the card to take
     * @param isUpper if the card is in the upper row in true, if the card is in the lower row it is false
     * @return always returns {@code true} (message is sent asynchronously)
     */
    @Override
    public boolean takeCard(String nickname, int position, boolean isUpper) {
        return sendMessage(new TakeCardMessage(nickname, position, isUpper));
    }

    /**
     * Sends a request to skip an extra draw phase.
     *
     * @param nickname the nickname of the player requesting to skip
     * @return always returns {@code true} (message is sent asynchronously)
     */
    @Override
    public boolean skipExtraDraw(String nickname) {
        return sendMessage(new SkipExtraDrawMessage(nickname));
    }

    /**
     * Registers the player in the lobby and stores the associated {@link ClientController}
     * for handling incoming server messages.
     *
     * @param nickname   the nickname chosen by the player
     * @param controller the {@link ClientController} to which server messages will be dispatched
     * @return an empty string, as the response is handled asynchronously
     */
    @Override
    public String getLobby(String nickname, ClientController controller) {
        this.controller = controller;
        sendMessage(new GetLobbyMessage(nickname));
        return "";
    }

    /**
     * Sends a request to create a new game with the specified settings.
     *
     * @param nickname            the nickname of the player creating the game
     * @param expectedNumPlayers  the number of players expected to join the game
     * @param color               the color chosen by the creating player
     * @param viewId              the identifier of the player's virtual view (unused over socket)
     * @return always returns {@code true} (message is sent asynchronously)
     */
    @Override
    public boolean createNewGame(String nickname, int expectedNumPlayers, Color color, String viewId) {
        return sendMessage(new CreateGameMessage(expectedNumPlayers, color));
    }

    /**
     * Sends a request to join an existing game.
     *
     * @param nickname the nickname of the player joining the game
     * @param id       the identifier of the game to join
     * @param color    the color chosen by the joining player
     * @param viewId   the identifier of the player's virtual view (unused over socket)
     * @return always returns {@code true} (message is sent asynchronously)
     */
    @Override
    public boolean joinGame(String nickname, int id, Color color, String viewId) {
        return  sendMessage(new JoinGameMessage(id, color));
    }

    /**
     * Sends a serialized message to the server through the output stream.
     *
     * @param message the {@link Message} to be sent
     * @return {@code true} if the message was sent successfully;
     *         {@code false} if an I/O error occurred
     */
    private boolean sendMessage(Message message) {
        try {
            synchronized (out) {
                out.writeObject(message);
                out.flush();
                out.reset();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Continuously listens for messages coming from the server.
     * Each received message is executed on the client side using
     * the associated {@link ClientController}.
     * This method runs on a dedicated thread started at construction time.
     */
    private void listenFromServer() {
        try {
            while (true) {
                Message message = (Message) in.readObject();
                message.executeClientSide(controller);
            }
        } catch (IOException | ClassNotFoundException e) {
            keepAlive.stop(); // ferma il keepalive se cade la connessione
            e.printStackTrace();
        }
    }
}