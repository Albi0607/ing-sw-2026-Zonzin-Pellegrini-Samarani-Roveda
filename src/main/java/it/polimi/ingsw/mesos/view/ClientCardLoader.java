package it.polimi.ingsw.mesos.view;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.common.BuildingCardJson;
import it.polimi.ingsw.mesos.common.CharacterCardJson;
import it.polimi.ingsw.mesos.common.EventCardJson;
import it.polimi.ingsw.mesos.common.*;

import java.io.IOException;
import java.util.List;

public class ClientCardLoader {
    
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

    private static void loadEventCards() throws IOException {
        List<EventCardJson> list = OpenFileJson.loadList(
                "events.json",
                new TypeReference<List<EventCardJson>>() {}
        );

        for (EventCardJson j : list) {
            CardRegistry.registerCard(j.id, j);
        }
    }

    private static void loadCharacterCards() throws IOException{
        List<CharacterCardJson> list = OpenFileJson.loadList(
                "characters.json",
                new TypeReference<List<CharacterCardJson>>() {}
        );

        for (CharacterCardJson j : list) {
            CardRegistry.registerCard(j.id, j);
        }
    }

    private static void loadBuildingCards() throws IOException{
        List<BuildingCardJson> list = OpenFileJson.loadList(
                "buildings.json",
                new TypeReference<List<BuildingCardJson>>() {}
        );

        for (BuildingCardJson j : list) {
            CardRegistry.registerCard(j.id, j);
        }
    }
}
