
package it.polimi.ingsw.mesos.view.CLI;
import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.rete.ClientChoseSetup;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.view.ClientCardLoader;

import java.util.Scanner;

public class CLIClient {

    public static void main(String[] args) {

        System.out.println("⏳ Caricamento registry carte locale...");

        try {
            // Caricamento asset locali
            ClientCardLoader.loadAllCards();
            VisualTheme.init();
            System.out.println(CLIPrinter.ANSI_GREEN + "✔ Asset caricati con successo!" + CLIPrinter.ANSI_RESET);
        } catch (Exception e) {
            System.err.println("Attenzione: Errore nel caricamento dei file JSON: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        Network network = null;

        while (true) {
            System.out.println(CLIPrinter.ANSI_CYAN + "\n=== CONFIGURAZIONE RETE ===" + CLIPrinter.ANSI_RESET);
            System.out.println("⏳ Ricerca automatica server LAN in corso...");
            String serverIp = "";
            int port = 0;
            String netChoice = "";

            String[] serverInfo = it.polimi.ingsw.mesos.rete.ServerDiscoverer.discoverServerInfo();

            if (serverInfo != null) {
                // --- SERVER TROVATO ---
                serverIp = serverInfo[0];
                int socketPort = Integer.parseInt(serverInfo[1]);
                int rmiPort = Integer.parseInt(serverInfo[2]);

                System.out.println(CLIPrinter.ANSI_GREEN + "✔ Server trovato!" + CLIPrinter.ANSI_RESET);
                System.out.println("IP: " + serverIp);
                System.out.println("\nScegli il protocollo:");
                System.out.println(" 1) SOCKET");
                System.out.println(" 2) RMI");
                System.out.print("> ");

                String choice = scanner.nextLine().trim();
                if (choice.equals("1") || choice.equalsIgnoreCase("SOCKET")) {
                    netChoice = "SOCKET";
                    port = socketPort;
                } else {
                    netChoice = "RMI";
                    port = rmiPort;
                }

            } else {
                // --- FALLBACK MANUALE ---
                System.out.println(CLIPrinter.ANSI_RED + "❌ Nessun server trovato automaticamente." + CLIPrinter.ANSI_RESET);
                System.out.print("\nInserisci IP manualmente (o 'local'): ");
                String userInput = scanner.nextLine().trim();
                serverIp = userInput.equalsIgnoreCase("local") ? "127.0.0.1" : userInput;

                while (true) {
                    System.out.println("\nScegli il protocollo di connessione:");
                    System.out.println(" - RMI");
                    System.out.println(" - SOCKET");
                    System.out.print("> ");
                    netChoice = scanner.nextLine().trim().toUpperCase();

                    if (netChoice.equals("RMI")) {
                        port = 1099; // Default fallback
                        break;
                    } else if (netChoice.equals("SOCKET")) {
                        port = 1234; // Default fallback
                        break;
                    } else {
                        System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida." + CLIPrinter.ANSI_RESET);
                    }
                }
            }

            System.out.println("\n⏳ Connessione al server " + serverIp + " sulla porta " + port + " tramite " + netChoice + " in corso...");

            try {
                // Tenta la connessione
                network = ClientChoseSetup.createNetwork(netChoice, serverIp, port);
                System.out.println(CLIPrinter.ANSI_GREEN + "✔ Connessione stabilita con successo!" + CLIPrinter.ANSI_RESET);

                break;

            } catch (Exception e) {
                // Se la connessione fallisce, stampiamo l'errore e il ciclo ricomincerà da capo!
                System.out.println(CLIPrinter.ANSI_RED + "❌ Impossibile connettersi al server all'IP: " + serverIp + CLIPrinter.ANSI_RESET);
                System.out.println("Assicurati che il Server sia acceso, che l'IP sia corretto e che il firewall non blocchi la connessione.");
                System.out.println("Riprova.\n");
            }
        }


        try {
            System.out.print("\nMySQL username (invio per skip): ");
            String user = scanner.nextLine().trim();

            String password = "";
            if (!user.isBlank()) {
                System.out.print("MySQL password: ");
                password = scanner.nextLine().trim();
            }

            DBManager.init(user, password); // ← chiamato SEMPRE, anche con user vuoto
            CLI cli = new CLI();

            ClientController controller = new ClientController(cli, network);

            cli.setController(controller);

            cli.start();

        } catch (Exception e) {
            System.err.println(CLIPrinter.ANSI_RED + "❌ Errore critico durante l'avvio del client: " + e.getMessage() + CLIPrinter.ANSI_RESET);
            e.printStackTrace();
        }
    }
}

