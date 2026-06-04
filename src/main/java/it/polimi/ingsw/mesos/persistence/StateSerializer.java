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
 * Saves and restores the initial game state (decks and player order) to disk.
 * This data represents the "random" component generated at the beginning that is not captured by move logs.
 */
public class StateSerializer {

    private final String tribeFile;
    private final String buildingFile;
    private final String orderFile;

    /**
     * Constructs a StateSerializer for a specific game ID.
     *
     * @param gameId the unique identifier for the game
     */
    public StateSerializer(int gameId) {
        this.tribeFile    = "mesos_tribe_"    + gameId + ".txt";
        this.buildingFile = "mesos_building_" + gameId + ".txt";
        this.orderFile    = "mesos_order_"    + gameId + ".txt";
    }

    /**
     * Saves the initial player order (after the shuffle in startGame).
     *
     * @param players the list of players in their starting order
     */
    public void savePlayerOrder(List<it.polimi.ingsw.mesos.model.Player> players) {
        List<String> nicknames = new ArrayList<>();
        for (it.polimi.ingsw.mesos.model.Player p : players) {
            nicknames.add(p.getNickname());
        }
        writeIdsToFile(nicknames, orderFile);
    }

    /**
     * Restores the saved player order.
     *
     * @return the list of player nicknames in their saved order, or null if no order was saved
     */
    public List<String> restorePlayerOrder() {
        if (!Files.exists(Paths.get(orderFile))) return null;
        return readIdsFromFile(orderFile);
    }

    /**
     * Saves the current deck state by draining it, writing the card IDs to disk in order,
     * and then restoring the deck to its original state.
     * The cards are re-inserted in reverse order to maintain the original sequence,
     * as the deck's put() method adds cards to the top.
     *
     * @param deck    the deck to save
     * @param isTribe true if it is a tribe deck, false for a building deck
     * @param <T>     the type of cards in the deck
     */
    public <T extends Card> void saveDeck(Deck<T> deck, boolean isTribe) {
        List<T> cards = drainDeck(deck);
        String path = isTribe ? tribeFile : buildingFile;
        List<String> ids = new ArrayList<>();
        for (T card : cards) {
            ids.add(card.getId());
        }
        writeIdsToFile(ids, path);
        for (int i = cards.size() - 1; i >= 0; i--) {
            deck.put(cards.get(i));
        }
    }


    /**
     * Loads the saved deck order from disk and applies it to the current deck.
     * This method reconstructs the deck by matching saved IDs against the full pool
     * of available cards (loaded from JSON configurations).
     * The current deck is drained before the saved cards are re-inserted in the correct order.
     *
     * @param currentDeck the deck to restore
     * @param isTribe     true if it is a tribe deck, false for a building deck
     * @param <T>         the type of cards in the deck
     * @return true if restoration was successful, false otherwise
     */
    public <T extends Card> boolean restoreDeck(Deck<T> currentDeck, boolean isTribe) {
        String path = isTribe ? tribeFile : buildingFile;

        if (!Files.exists(Paths.get(path))) {
            System.err.println("[StateSerializer] File not found: " + path);
            return false;
        }

        if(isTribe) {
            List<TribeCard> allCards = new ArrayList<>();
            allCards.addAll(new CreateCharacterCard("cards/characters.json").getAllCharacterCards());
            allCards.addAll(new CreateEventCard("cards/events.json").getAllEventCards());
            drainDeck(currentDeck);
            java.util.Map<String, TribeCard> byId = new java.util.HashMap<>();
            for (TribeCard card : allCards) {
                byId.put(card.getId(), card);
            }
            List<String> savedIds = readIdsFromFile(path);
            if (savedIds == null || savedIds.isEmpty()) return false;

            for (int i = savedIds.size() - 1; i >= 0; i--) {
                TribeCard card = byId.get(savedIds.get(i));
                if (card != null) currentDeck.put((T) card);
                else System.err.println("[StateSerializer] Tribe card not found for ID: " + savedIds.get(i));
            }
            System.out.println("[StateSerializer] Tribe deck restored (" + savedIds.size() + " cards)");
            return true;
        }
        else {
            List<BuildingCard> allCards = new ArrayList<>();
            allCards.addAll(new CreateBuildingCard("cards/buildings.json").getAllBuildingCards());
            java.util.Map<String, BuildingCard> byId = new java.util.HashMap<>();
            for (BuildingCard card : allCards) {
                byId.put(card.getId(), card);
            }
            drainDeck(currentDeck);
            List<String> savedIds = readIdsFromFile(path);
            if (savedIds == null || savedIds.isEmpty()) return false;

            for (int i = savedIds.size() - 1; i >= 0; i--) {
                BuildingCard card = byId.get(savedIds.get(i));
                if (card != null) currentDeck.put((T) card);
                else System.err.println("[StateSerializer] Building card not found for ID: " + savedIds.get(i));
            }
            System.out.println("[StateSerializer] Building deck restored (" + savedIds.size() + " cards)");
            return true;
        }
    }

    /**
     * Deletes the persistence files for this game.
     */
    public void delete() {
        try {
            Files.deleteIfExists(Paths.get(tribeFile));
            Files.deleteIfExists(Paths.get(buildingFile));
            Files.deleteIfExists(Paths.get(orderFile));
            System.out.println("[StateSerializer] Persistence files deleted.");
        } catch (IOException e) {
            System.err.println("[StateSerializer] Error deleting files: " + e.getMessage());
        }
    }

    /**
     * Checks if a saved state exists.
     *
     * @return true if all required persistence files exist
     */
    public boolean hasSavedState() {
        return Files.exists(Paths.get(tribeFile))
                && Files.exists(Paths.get(buildingFile))
                && Files.exists(Paths.get(orderFile));
    }

    /**
     * Drains all cards from a deck and returns them as a list.
     *
     * @param deck the deck to drain
     * @param <T>  the type of cards in the deck
     * @return a list containing all cards that were in the deck
     */
    private <T extends Card> List<T> drainDeck(Deck<T> deck) {
        List<T> cards = new ArrayList<>();
        while (!deck.isEmpty()) {
            cards.add(deck.draw());
        }
        return cards;
    }

    /**
     * Serializes a list of strings (IDs or nicknames) and writes them to a file.
     *
     * @param ids  the list of strings to serialize
     * @param path the destination file path
     */
    private void writeIdsToFile(List<String> ids, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path))) {
            oos.writeObject(ids);
            oos.flush();
        } catch (IOException e) {
            System.err.println("[StateSerializer] Error writing to " + path + ": " + e.getMessage());
        }
    }

    /**
     * Deserializes a list of strings from a file.
     *
     * @param path the source file path
     * @return the deserialized list of strings, or null if an error occurs
     */
    @SuppressWarnings("unchecked")
    private List<String> readIdsFromFile(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path))) {
            return (List<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[StateSerializer] Error reading from " + path + ": " + e.getMessage());
            return null;
        }
    }
}
