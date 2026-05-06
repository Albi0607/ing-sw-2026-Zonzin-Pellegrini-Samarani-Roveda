
package it.polimi.ingsw.mesos.view;
import it.polimi.ingsw.mesos.rete.ClientChoseSetup;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.view.CLI.CLI;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;
import it.polimi.ingsw.mesos.view.CLI.VisualTheme;

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
        String netChoice = "";

        while (true) {
            System.out.println("\nScegli il protocollo di connessione:");
            System.out.println(" - RMI");
            System.out.println(" - SOCKET");
            System.out.print("Scelta: ");
            netChoice = scanner.nextLine().trim().toUpperCase();

            if (netChoice.equals("RMI") || netChoice.equals("SOCKET")) {
                break;
            } else {
                System.out.println(CLIPrinter.ANSI_RED + "❌ Scelta non valida. Digita 'RMI' o 'SOCKET'." + CLIPrinter.ANSI_RESET);
            }
        }

        System.out.println("⏳ Connessione al server tramite " + netChoice + " in corso...");

        try {
            Network network = ClientChoseSetup.createNetwork(netChoice);

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

