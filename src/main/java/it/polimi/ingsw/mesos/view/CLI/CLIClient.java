
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

            System.out.print("Inserisci IP del Server (o 'local' per 127.0.0.1): ");
            String userInput = scanner.nextLine().trim();
            String serverIp = userInput.equalsIgnoreCase("local") ? "127.0.0.1" : userInput;

            String netChoice = "";
            int port = 0;

            while (true) {
                System.out.println("\nScegli il protocollo di connessione:");
                System.out.println(" 1) SOCKET (Porta default: 1234)");
                System.out.println(" 2) RMI    (Porta default: 1099)");
                System.out.print("> ");
                String choice = scanner.nextLine().trim().toUpperCase();

                if (choice.equals("1") || choice.equals("SOCKET")) {
                    netChoice = "SOCKET";
                    port = 1234;
                    break;
                } else if (choice.equals("2") || choice.equals("RMI")) {
                    netChoice = "RMI";
                    port = 1099;
                    break;
                } else {
                    System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida." + CLIPrinter.ANSI_RESET);
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

