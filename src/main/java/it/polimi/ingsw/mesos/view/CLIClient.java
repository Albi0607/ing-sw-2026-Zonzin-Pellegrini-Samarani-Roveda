
package it.polimi.ingsw.mesos.view;
import it.polimi.ingsw.mesos.view.CLI.CLI;
import it.polimi.ingsw.mesos.view.CLI.VisualTheme;

public class CLIClient {

    public static void main(String[] args) {

        System.out.println("⏳ Caricamento registry carte locale...");

        try {
            ClientCardLoader.loadAllCards();

            VisualTheme.init();

        } catch (Exception e) {
            System.err.println("Attenzione: Errore nel caricamento dei file JSON: " + e.getMessage());
        }

        CLI cli = new CLI();
        cli.start();
    }

}

