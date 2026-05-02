package it.polimi.ingsw.mesos.view;
import java.util.HashMap;
import java.util.Map;


public class CardRegistry {

    private static final Map<String, Object> database = new HashMap<>();

    /**
     * Registra le informazioni di una carta.
     * @param id L'identificativo univoco della carta
     * @param cardJsonObject L'oggetto JSON mappato da Jackson (es. EventCardJson)
     */
    public static void registerCard(String id, Object cardJsonObject) {
        if (id != null && cardJsonObject != null) {
            database.put(id, cardJsonObject);
        }
    }

    /**
     * Recupera le informazioni della carta.
     * Il Client userà questo metodo e farà il cast alla classe giusta (es. (EventCardJson) registry.getCardInfo(id))
     */
    public static Object getCardInfo(String id) {
        return database.get(id);
    }

    // Utilità per controllare se il DB è pieno
    public static int size() {
        return database.size();
    }
}
