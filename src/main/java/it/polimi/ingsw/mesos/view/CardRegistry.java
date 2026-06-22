package it.polimi.ingsw.mesos.view;
import it.polimi.ingsw.mesos.common.CardJson;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code CardRegistry} class serves as a central, static repository for storing and
 * retrieving {@link CardJson} objects.
 * <p>
 * This registry acts as an in-memory database using a {@link Map} to associate
 * unique card identifiers (as {@code String}) with their corresponding {@link CardJson} instances.
 */
public class CardRegistry {
    private static final Map<String, CardJson> database = new HashMap<>();

    /**
     * Registers a card into the registry.
     * * @param card the {@link CardJson} object to be registered. The card must not be null
     * and must have a valid ID.
     */
    public static void registerCard(CardJson card) {
        if (card != null && card.getId() != null) {
            database.put(card.getId(), card);
        }
    }

    /**
     * Retrieves a card from the registry by its unique identifier.
     * * @param id the unique identifier of the card.
     * @return the {@link CardJson} associated with the given ID, or {@code null} if no
     * card exists with that ID.
     */
    public static CardJson getCard(String id) {
        return database.get(id);
    }


    /**
     * Retrieves a card from the registry and attempts to cast it to the specified class type.
     * * @param <T> the type of the card subclass to return.
     * @param id  the unique identifier of the card.
     * @param cls the {@link Class} object representing the desired type.
     * @return the card cast to the specified type, or {@code null} if the card is not found
     * or is not an instance of the specified class.
     */
    public static <T extends CardJson> T getCard(String id, Class<T> cls) {
        CardJson card = database.get(id);

        if (cls.isInstance(card)) {
            return cls.cast(card);
        }

        return null;
    }

    /**
     * Returns the total number of cards currently stored in the registry.
     * * @return the size of the registry database.
     */
    public static int size() {
        return database.size();
    }
}

