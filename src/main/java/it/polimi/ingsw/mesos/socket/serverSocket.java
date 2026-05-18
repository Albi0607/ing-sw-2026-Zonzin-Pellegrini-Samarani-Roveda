package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.multipleGames.ServerState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class serverSocket {

    /**
     * Avvia il server socket. La gestione dell'interazione con i client connessi
     * è delegata alla classe ClientHandler facendo partire un thread con un istanza per
     * ciascun client.
     *
     * @param serverState stato condiviso con il server RMI
     * @param port        porta TCP su cui ascoltare
     */
    public void start(ServerState serverState, int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Socket pronto in attesa di connessioni");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New SOCKET connection/request from: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket, serverState);

                Thread thread = new Thread(handler);
                thread.setDaemon(true); // il thread non blocca la JVM in chiusura, al contrario avrebbe dovuto aspettare che le connessioni finissero prima di potersi chiudere
                thread.setName("client-" + clientSocket.getInetAddress().getHostAddress());

                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Fatal error: " + e.getMessage());
        }
    }
}