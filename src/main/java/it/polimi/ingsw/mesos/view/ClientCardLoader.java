package it.polimi.ingsw.mesos.view;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.common.BuildingCardJson;
import it.polimi.ingsw.mesos.common.CharacterCardJson;
import it.polimi.ingsw.mesos.common.EventCardJson;
import it.polimi.ingsw.mesos.common.*;

import java.io.IOException;
import java.util.List;

/**
 * The {@code ClientCardLoader} class is responsible for the initialization phase of the client application.
 * <p>
 * It handles the loading of various card types (Events, Characters, and Buildings) from external
 * JSON resource files and populates the {@link CardRegistry} to make the card data available
 * throughout the application.
 */
public class ClientCardLoader {

    /**
     * Orchestrates the loading process of all card categories.
     * <p>
     * This method attempts to load event, character, and building cards sequentially.
     * * @throws RuntimeException if an {@link IOException} occurs during the loading of any
     * resource file, indicating that critical data is missing or corrupted.
     */
    public static void loadAllCards() {
        try {
            loadEventCards();
            loadCharacterCards();
            loadBuildingCards();
            System.out.println("Tutti gli asset del Client sono caricati nel CardRegistry!");

        } catch (IOException e) {
            System.err.println("ERRORE CRITICO: Impossibile caricare i file JSON del Client!");
            e.printStackTrace();
            throw new RuntimeException("Avvio fallito: file JSON mancanti o corrotti.", e);
        }
    }

    /**
     * Loads event cards from the specified JSON file and registers them in the {@link CardRegistry}.
     * * @throws IOException if the file cannot be read or parsed correctly.
     */
    private static void loadEventCards() throws IOException {
        List<EventCardJson> list = OpenFileJson.loadList(
                "cards/events.json",
                new TypeReference<List<EventCardJson>>() {}
        );

        for (EventCardJson j : list) {
            CardRegistry.registerCard(j);
        }
    }

    /**
     * Loads character cards from the specified JSON file and registers them in the {@link CardRegistry}.
     * * @throws IOException if the file cannot be read or parsed correctly.
     */
    private static void loadCharacterCards() throws IOException{
        List<CharacterCardJson> list = OpenFileJson.loadList(
                "cards/characters.json",
                new TypeReference<List<CharacterCardJson>>() {}
        );

        for (CharacterCardJson j : list) {
            CardRegistry.registerCard(j);
        }
    }

    /**
     * Loads building cards from the specified JSON file and registers them in the {@link CardRegistry}.
     * * @throws IOException if the file cannot be read or parsed correctly.
     */
    private static void loadBuildingCards() throws IOException{
        List<BuildingCardJson> list = OpenFileJson.loadList(
                "cards/buildings.json",
                new TypeReference<List<BuildingCardJson>>() {}
        );

        for (BuildingCardJson j : list) {
            CardRegistry.registerCard(j);
        }
    }
}
