package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.CreateBuildingCard;
import it.polimi.ingsw.mesos.model.deck.CreateCharacterCard;
import it.polimi.ingsw.mesos.model.deck.CreateEventCard;
import it.polimi.ingsw.mesos.model.deck.Deck;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Salva e ripristina lo stato iniziale della partita (mazzi e ordine giocatori) su disco.
 * Questi dati rappresentano la componente "casuale" generata all'inizio che non è catturata dai log delle mosse.
 */
public class StateSerializer {

    private final String tribeFile;
    private final String buildingFile;
    private final String orderFile;

    public StateSerializer(int gameId) {
        this.tribeFile    = "mesos_tribe_"    + gameId + ".txt";
        this.buildingFile = "mesos_building_" + gameId + ".txt";
        this.orderFile    = "mesos_order_"    + gameId + ".txt";
    }

    /**
     * Salva l'ordine iniziale dei giocatori (dopo lo shuffle di startGame).
     */
    public void savePlayerOrder(List<it.polimi.ingsw.mesos.model.Player> players) {
        List<String> nicknames = new ArrayList<>();
        for (it.polimi.ingsw.mesos.model.Player p : players) {
            nicknames.add(p.getNickname());
        }
        writeIdsToFile(nicknames, orderFile);
    }

    /**
     * Ripristina l'ordine dei giocatori salvato.
     */
    public List<String> restorePlayerOrder() {
        if (!Files.exists(Paths.get(orderFile))) return null;
        return readIdsFromFile(orderFile);
    }

    /**
     * Svuota il mazzo, salva gli ID su disco e lo ripristina.
     */
    public <T extends Card> void saveDeck(Deck<T> deck, boolean isTribe) {
        List<T> cards = drainDeck(deck);
        String path = isTribe ? tribeFile : buildingFile;
        List<String> ids = new ArrayList<>();
        for (T card : cards) {
            ids.add(card.getId());
        }
        writeIdsToFile(ids, path);
        // Rimette le carte in ordine inverso (put() aggiunge in cima)
        for (int i = cards.size() - 1; i >= 0; i--) {
            deck.put(cards.get(i));
        }
    }


    /**
     * Carica l'ordine salvato e lo applica al mazzo attuale.
     */
    public <T extends Card> boolean restoreDeck(Deck<T> currentDeck, boolean isTribe) {
        String path = isTribe ? tribeFile : buildingFile;

        if (!Files.exists(Paths.get(path))) {
            System.err.println("[StateSerializer] File non trovato: " + path);
            return false;
        }

        if(isTribe) {
            // creo il pool intero di carte tribe da cui matchare gli indici (SOLUZIONE PIù ESTENDIBILE??)
            List<TribeCard> allCards = new ArrayList<>();
            allCards.addAll(new CreateCharacterCard("cards/characters.json").getAllCharacterCards());
            allCards.addAll(new CreateEventCard("cards/events.json").getAllEventCards());
            // Svuota il deck attuale (quello generato casualmente)
            drainDeck(currentDeck);
            // itera in tutto il pool per matchare l'id
            java.util.Map<String, TribeCard> byId = new java.util.HashMap<>();
            for (TribeCard card : allCards) {
                byId.put(card.getId(), card);
            }
            // Legge l'ordine originale dal file
            List<String> savedIds = readIdsFromFile(path);
            if (savedIds == null || savedIds.isEmpty()) return false;

            for (int i = savedIds.size() - 1; i >= 0; i--) {
                TribeCard card = byId.get(savedIds.get(i));
                if (card != null) currentDeck.put((T) card);
                else System.err.println("[StateSerializer] Carta tribe non trovata per id: " + savedIds.get(i));
            }
            System.out.println("[StateSerializer] Tribe deck ripristinato (" + savedIds.size() + " carte)");
            return true;
        }
        else {
            // creo il pool intero di carte building da cui matchare gli indici (SOLUZIONE PIù ESTENDIBILE??)
            List<BuildingCard> allCards = new ArrayList<>();
            allCards.addAll(new CreateBuildingCard("cards/buildings.json").getAllBuildingCards());
            // itera in tutto il pool per matchare l'id
            java.util.Map<String, BuildingCard> byId = new java.util.HashMap<>();
            for (BuildingCard card : allCards) {
                byId.put(card.getId(), card);
            }
            // Svuota il deck attuale (quello generato casualmente)
            drainDeck(currentDeck);
            // Legge l'ordine originale dal file
            List<String> savedIds = readIdsFromFile(path);
            if (savedIds == null || savedIds.isEmpty()) return false;

            for (int i = savedIds.size() - 1; i >= 0; i--) {
                BuildingCard card = byId.get(savedIds.get(i));
                if (card != null) currentDeck.put((T) card);
                else System.err.println("[StateSerializer] Carta building non trovata per id: " + savedIds.get(i));
            }
            System.out.println("[StateSerializer] Building deck ripristinato (" + savedIds.size() + " carte)");
            return true;
        }
    }

    /**
     * Elimina i file di persistenza di questa partita.
     */
    public void delete() {
        try {
            Files.deleteIfExists(Paths.get(tribeFile));
            Files.deleteIfExists(Paths.get(buildingFile));
            Files.deleteIfExists(Paths.get(orderFile));
            System.out.println("[StateSerializer] File persistenza eliminati.");
        } catch (IOException e) {
            System.err.println("[StateSerializer] Errore eliminazione file: " + e.getMessage());
        }
    }

    public boolean hasSavedState() {
        return Files.exists(Paths.get(tribeFile))
                && Files.exists(Paths.get(buildingFile))
                && Files.exists(Paths.get(orderFile));
    }

    private <T extends Card> List<T> drainDeck(Deck<T> deck) {
        List<T> cards = new ArrayList<>();
        while (!deck.isEmpty()) {
            cards.add(deck.draw());
        }
        return cards;
    }

    private void writeIdsToFile(List<String> ids, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path))) {
            oos.writeObject(ids);
            oos.flush();
        } catch (IOException e) {
            System.err.println("[StateSerializer] Errore scrittura " + path + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> readIdsFromFile(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path))) {
            return (List<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[StateSerializer] Errore lettura " + path + ": " + e.getMessage());
            return null;
        }
    }
}
