
package it.polimi.ingsw.mesos.view.CLI;
import it.polimi.ingsw.mesos.network.ClientChoseSetup;
import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.network.Network;
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
            String userInputS = scanner.nextLine().trim();
            String serverIp = userInputS.equalsIgnoreCase("local") ? "127.0.0.1" : userInputS;

            System.out.print("Inserisci IP del Client (o 'local' per 127.0.0.1): ");
            String userInputC = scanner.nextLine().trim();
            String clientIp = userInputC.equalsIgnoreCase("local") ? "127.0.0.1" : userInputC;

            try {
                java.net.InetAddress inet = java.net.InetAddress.getByName(clientIp);
                java.net.NetworkInterface ni = java.net.NetworkInterface.getByInetAddress(inet);

                if (ni == null && !clientIp.equals("127.0.0.1")) {
                    System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: L'IP " + clientIp + " non appartiene a questo PC!" + CLIPrinter.ANSI_RESET);
                    System.out.println("Assicurati di aver digitato il tuo vero indirizzo IP locale.\n");
                    continue;
                }
            } catch (Exception e) {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Errore: Formato IP non valido." + CLIPrinter.ANSI_RESET);
                System.out.println();
                continue;
            }

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
                network = ClientChoseSetup.createNetwork(netChoice, serverIp, port, clientIp);
                System.out.println(CLIPrinter.ANSI_GREEN + "✔ Connessione stabilita con successo!" + CLIPrinter.ANSI_RESET);
                break;

            } catch (Exception e) {
                // Se la connessione fallisce, stampiamo l'errore e il ciclo ricomincerà da capo
                System.out.println(CLIPrinter.ANSI_RED + "❌ Impossibile connettersi al server all'IP: " + serverIp + CLIPrinter.ANSI_RESET);
                System.out.println("Assicurati che il Server sia acceso, che l'IP sia corretto e che il firewall non blocchi la connessione.");
                System.out.println("Riprova.\n");
            }
        }

        try {
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

