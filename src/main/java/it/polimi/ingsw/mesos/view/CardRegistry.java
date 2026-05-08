package it.polimi.ingsw.mesos.view;
import it.polimi.ingsw.mesos.common.CardJson;

import java.util.HashMap;
import java.util.Map;


public class CardRegistry {
    private static final Map<String, CardJson> database = new HashMap<>();

    public static void registerCard(CardJson card) {
        if (card != null && card.getId() != null) {
            database.put(card.getId(), card);
        }
    }

    public static CardJson getCard(String id) {
        return database.get(id);
    }

    public static <T extends CardJson> T getCard(String id, Class<T> cls) {
        CardJson card = database.get(id);

        if (cls.isInstance(card)) {
            return cls.cast(card);
        }

        return null;
    }

    public static int size() {
        return database.size();
    }
}

