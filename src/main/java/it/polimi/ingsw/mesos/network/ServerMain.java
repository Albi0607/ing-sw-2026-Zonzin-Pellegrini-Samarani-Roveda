package it.polimi.ingsw.mesos.network;

import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.RMI.server_RMI;
import it.polimi.ingsw.mesos.multipleGames.ServerState;
import it.polimi.ingsw.mesos.socket.serverSocket;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Class used to start the game server, which accepts clients and manages game creation.
 * The class launches two threads: one to handle all requests from clients implementing the RMI network protocol, and
 * another to spawn a thread for handling all requests from clients using a socket-based network implementation
 */
public class ServerMain {

    /**
     * Entry point of the server application.
     *
     * This method initializes the server environment, including:
     * - retrieval of the local network IP for RMI configuration,
     * - creation and initialization of the ServerState,
     * - restoration of previously saved games from disk,
     * - optional activation of the database connection,
     * - and startup of both RMI and Socket server instances in separate threads.
     *
     * The server supports dual communication protocols (RMI and Socket)
     * and is ready to accept client connections after initialization.
     *
     * @param args optional command-line arguments; if provided, the first argument
     *             is used to override the default RMI port
     * @throws SQLException if a database-related error occurs during initialization
     */
    public static void main(String[] args) throws SQLException {
        String myIp = "127.0.0.1";

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== CONFIGURAZIONE SERVER ===");
        System.out.print("Inserisci l'IP di questo Server (o premi invio per 127.0.0.1): ");
        // FORZA RMI ad usare questo IP per evitare che i client si connettano a se stessi!
        String inputIp = scanner.nextLine().trim();

        // Controllo di sicurezza: se premi solo invio, usa il localhost
        if (!inputIp.isEmpty()) {
            myIp = inputIp;
        }

        System.setProperty("java.rmi.server.hostname", myIp);

        //aggiungo serverState e lobby per la gestione delle partite
        ServerState serverState = new ServerState();
        //scansione del disco prima dell'avvio di un server per il ripristino delle partite interrotte
        serverState.initializeFromDisk();

        int port = 1099;

        boolean dbEnabled = false;

        System.out.print("MySQL username (invio per skip): ");
        String user = scanner.nextLine();

        String password = null;

        if (!user.isBlank()) {
            System.out.print("MySQL password: ");
            password = scanner.nextLine();
        }

        try {
            DBManager.init(user, password);
            dbEnabled = true;
            if (!DBManager.isActive()) {
                System.out.println("Database non attivato");
            } else {
                System.out.println("✔ Database attivato");
            }

        } catch (Exception e) {
            System.out.println("⚠ DB non disponibile → modalità senza database");
            dbEnabled = false;
        }

        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        final int finalPort = port;
        final int socketPort = 1234;
        final String finalIp = myIp;

        //RMI THREAD
        new Thread(() -> {
            server_RMI rmi = new server_RMI();
            rmi.start(serverState, finalIp, finalPort);
        }).start();

        //SOCKET THREAD
        new Thread(() -> {
            serverSocket socket = new serverSocket();
            // 1234 per il socket per non andare in conflitto con RMI (1099)
            socket.start(serverState, socketPort);
        }).start();

        System.out.println("\n======================================");
        System.out.println("SERVER AVVIATI E PRONTI ALLE CONNESSIONI");
        System.out.println("IP Rete Locale: " + finalIp);
        System.out.println("Porta RMI:      " + finalPort);
        System.out.println("Porta Socket:   " + socketPort);
        System.out.println("======================================\n");
    }
}
