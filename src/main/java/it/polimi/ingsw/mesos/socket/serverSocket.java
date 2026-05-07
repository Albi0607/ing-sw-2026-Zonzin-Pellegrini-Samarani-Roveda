package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.persistence.GameRestorer;
import it.polimi.ingsw.mesos.persistence.MoveLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class serverSocket {

    private final List<ClientHandler> clients = new ArrayList<>();
    //potrebbe servire non mi è ancora chiaro se il server deve in qualche modo usare questa lista per iterare/osservare i client (es. gestione disconnessioni)

    public void start(GameController controller, int port) {
        MoveLogger logger  = controller.getMoveLogger();
        GameRestorer restorer = new GameRestorer(logger);

        // Controlla se c'è una partita salvata da ripristinare
        if (logger.hasSavedGame()) {
            System.out.println("[Server] Partita salvata trovata. " +
                    "I giocatori devono riconnettersi con gli stessi nickname.");

            // Il replay avverrà automaticamente in handleRegister() del ClientHandler
            // una volta che tutti i giocatori attesi si sono riconnessi.

            // Passiamo il restorer al controller in modo che lo usi.
            controller.setRestorer(restorer);
        }
        // ricorda: notazione confusionaria, esiste serverSocket di tipo ServerSocket e serverSocket l'attuale classe, due cose diverse
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening. Waiting for players...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from: "
                        + clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket, controller);
                clients.add(handler);

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