package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import it.polimi.ingsw.mesos.RMI.server_RMI;
import it.polimi.ingsw.mesos.multipleGames.ServerState;
import it.polimi.ingsw.mesos.socket.serverSocket;
import java.net.InetAddress;

import java.sql.SQLException;
import java.util.Scanner;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Collections;

/**
 * Class used to start the game server, which accepts clients and manages game creation.
 * The class launches two threads: one to handle all requests from clients implementing the RMI network protocol, and
 * another to spawn a thread for handling all requests from clients using a socket-based network implementation
 */
public class ServerMain {

    public static String getLocalIPv4() {
        try {
            System.out.println("🔍 Scansione interfacce di rete in corso...");
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {

                // Ignoriamo le interfacce spente, di loopback o virtuali
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }

                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {

                    // Vogliamo solo indirizzi IPv4
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();

                        // Evitiamo gli IP APIPA o di default
                        if (!ip.startsWith("169.254")) {
                            System.out.println("✔ Interfaccia trovata: " + ni.getDisplayName());
                            System.out.println("✔ IP assegnato: " + ip);
                            return ip;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Errore durante la scansione della rete: " + e.getMessage());
        }

        System.out.println("⚠ Nessuna interfaccia valida trovata. Fallback su localhost.");
        return "127.0.0.1";
    }

    public static void main(String[] args) throws SQLException {

        String myIp = getLocalIPv4();
        try {
            // FORZA RMI ad usare questo IP per evitare che i client si connettano a se stessi!
            System.setProperty("java.rmi.server.hostname", myIp);
        } catch (Exception e) {
            System.err.println("Impossibile recuperare l'IP di rete locale, userò localhost.");
        }

        //aggiungo serverState e lobby per la gestione delle partite
        ServerState serverState = new ServerState();
        //scansione del disco prima dell'avvio di un server per il ripristino delle partite interrotte
        serverState.initializeFromDisk();

        int port = 1099;

        boolean dbEnabled = false;

        Scanner scanner = new Scanner(System.in);

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
                System.out.println("Database non attivato");}
            else System.out.println("✔ Database attivato");

        } catch (Exception e) {
            System.out.println("⚠ DB non disponibile → modalità senza database");
            dbEnabled = false;
        }

        if(args.length>0){
            port=Integer.parseInt(args[0]);
        }
        final int finalPort = port;

        final int socketPort = 1234;

        final String finalIp = myIp;

        //RMI THREAD
        new Thread(() -> {
            server_RMI rmi = new server_RMI();
            rmi.start(serverState, finalIp ,finalPort);
        }).start();

        //SOCKET THREAD
        new Thread(() -> {
            serverSocket socket = new serverSocket();
            // 1234 per il socket per non andare in conflitto con RMI (1099)
            socket.start(serverState, socketPort );
        }).start();

        Thread broadcasterThread =
                new Thread(new UDPBroadcaster(
                        myIp,
                        socketPort,
                        finalPort
                ));

        broadcasterThread.setDaemon(true);
        broadcasterThread.start();

        System.out.println("\n======================================");
        System.out.println("SERVER AVVIATI E PRONTI ALLE CONNESSIONI");
        System.out.println("IP Rete Locale: " + finalIp);
        System.out.println("Porta RMI:      " + finalPort);
        System.out.println("Porta Socket:   " + socketPort);
        System.out.println("======================================\n");

    }

}
