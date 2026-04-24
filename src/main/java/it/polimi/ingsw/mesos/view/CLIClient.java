
package it.polimi.ingsw.mesos.view;

import it.polimi.ingsw.mesos.model.deck.CreateBuildingCard;
import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.model.deck.CreateEventCard;
import it.polimi.ingsw.mesos.view.CLI.CLI;
import it.polimi.ingsw.mesos.view.CLI.VisualTheme;

public class CLIClient {

    public static void main(String[] args) {

        System.out.println("⏳ Caricamento vocabolario carte locale...");

        try {
            // 1. IL CLIENT LEGGE I FILE E RIEMPIE IL SUO CARD REGISTRY
            new CreateCharacterCard("characters.json");
            new CreateEventCard("events.json");
            new CreateBuildingCard("buildings.json");

            System.out.println("Database caricato con successo!");

            VisualTheme.init();

        } catch (Exception e) {
            System.err.println("❌ Attenzione: Errore nel caricamento dei file JSON: " + e.getMessage());
        }

        CLI cli = new CLI();
        cli.start();
    }

}

