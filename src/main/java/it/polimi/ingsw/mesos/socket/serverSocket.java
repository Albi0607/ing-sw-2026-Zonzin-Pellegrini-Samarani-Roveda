package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.multipleGames.ServerState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The serverSocket class provides the implementation for the TCP socket server.
 * it listens for incoming connections and manages them by spawning dedicated
 * handler threads for each client.
 */
public class serverSocket {

    /**
     * Starts the socket server on the specified port.
     *
     * This method initializes a ServerSocket and enters a loop to accept incoming
     * client connections. For each accepted connection, it creates a new
     * ClientHandler instance and starts it in a separate daemon thread.
     *
     * Using daemon threads ensures that these connection-handling threads do not
     * prevent the Java Virtual Machine from shutting down when the main application exits.
     *
     * @param serverState the global state shared across all game instances and network protocols
     * @param port        the TCP port on which the server will listen for connections
     */
    public void start(ServerState serverState, int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Socket Server ready, listening for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New SOCKET connection/request from: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket, serverState);

                Thread thread = new Thread(handler);
                thread.setDaemon(true);
                thread.setName("client-" + clientSocket.getInetAddress().getHostAddress());

                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Fatal socket server error: " + e.getMessage());
        }
    }
}