package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.common.BuildingCardJson;
import it.polimi.ingsw.mesos.common.CharacterCardJson;
import it.polimi.ingsw.mesos.common.EventCardJson;

import java.util.HashMap;
import java.util.Map;

public class FormattersRegistry {
    private static final Map<Class<?>, Formatters.CardFormatter<?>> registry = new HashMap<>();

    static {
        registry.put(CharacterCardJson.class, new CharacterFormatter());
        registry.put(EventCardJson.class, new EventFormatter());
        registry.put(BuildingCardJson.class, new BuildingFormatter());
    }

    /**
     * Retrieves the specific formatter associated with a given card data class.
     * @param cls The class of the card JSON data object.
     * @param <T> The type of the card data.
     * @return The corresponding CardFormatter instance, or null if not registered.
     */
    @SuppressWarnings("unchecked")
    public static <T> Formatters.CardFormatter<T> getFormatter(Class<?> cls) {
        return (Formatters.CardFormatter<T>) registry.get(cls);
    }
}
